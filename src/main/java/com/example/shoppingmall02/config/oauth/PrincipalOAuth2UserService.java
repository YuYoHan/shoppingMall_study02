package com.example.shoppingmall02.config.oauth;

import com.example.shoppingmall02.config.jwt.JwtProvider;
import com.example.shoppingmall02.config.sercurity.PrincipalDetails;
import com.example.shoppingmall02.jwt.domain.ResponseTokenDTO;
import com.example.shoppingmall02.jwt.entity.TokenEntity;
import com.example.shoppingmall02.jwt.repository.TokenRepository;
import com.example.shoppingmall02.member.entity.MemberEntity;
import com.example.shoppingmall02.member.entity.SocialMemberEntity;
import com.example.shoppingmall02.member.repository.SocialMemberRepository;
import com.example.shoppingmall02.member.role.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class PrincipalOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final SocialMemberRepository socialMemberRepository;
    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // userRequest.getClientRegistration()은 인증 및 인가된 사용자 정보를 가져오는
        // Spring Security에서 제공하는 메서드입니다.
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        log.info("clientRegistration : " + clientRegistration);
        // 소셜 로그인 accessToken
        String tokenValue = userRequest.getAccessToken().getTokenValue();

        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService =
                new DefaultOAuth2UserService();
        log.info("oAuth2UserService : " + oAuth2UserService);

        // 소셜 로그인한 유저 정보를 가져온다.
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        log.info("oAuth2User : " + oAuth2User);

        // 회원가입 강제 진행
        OAuth2UserInfo oAuth2UserInfo = null;
        String registrationId = clientRegistration.getRegistrationId();
        log.info("registrationId : " + registrationId);

        // 구글 로그인
        if(registrationId.equals("google")) {
            log.info("구글 로그인");
            oAuth2UserInfo = new GoogleUser(oAuth2User, clientRegistration);
        } else {
            log.error("지원하지 않는 소셜 로그인입니다.");
        }

        // 네이버 로그인
        if(registrationId.equals("naver")) {
            oAuth2UserInfo = new NaverUser(oAuth2User, clientRegistration);
        } else {
            log.error("지원하지 않는 소셜 로그인입니다.");
        }

        // 사용자가 로그인한 소셜 서비스를 가지고 온다.
        // ex) google, naver
        String provider = oAuth2UserInfo.getProvider();
        // 사용자의 소셜 서비스에서 발급된 고유한 식별자를 가져온다.
        // 이 값은 해당 소셜 서비스에서 사용자를 식별하는 용도로 사용
        String providerId = oAuth2UserInfo.getProviderId();
        String memberName = oAuth2UserInfo.getName();
        String memberEmail = oAuth2UserInfo.getEmail();
        Auth role = Auth.USER;

        SocialMemberEntity findMember = socialMemberRepository.findByMemberEmail(memberEmail);
        if(findMember == null) {
            log.info("처음으로 소셜 로그인 접속합니다. \n" +"자동 회원가입을 진행합니다.");
            SocialMemberEntity save
                    = SocialMemberEntity.save(memberEmail, memberName, role, memberName, provider, providerId);
            log.info("유저 : " + save);
            findMember = socialMemberRepository.save(save);
        } else {
            log.info("기존에 소셜 로그인에 접속했습니다.");
            findMember.update(memberName);
        }

        // 권한 가져오기
        List<GrantedAuthority> authorities = getAuthoritiesForUser(findMember);
        // 토큰 생성
        ResponseTokenDTO tokenForOAuth2 =
                jwtProvider.createTokenForOAuth2(memberEmail, authorities, findMember.getMemberId());

        // 기존의 토큰이 있는지 확인
        TokenEntity findToken = tokenRepository.findByMemberEmail(tokenForOAuth2.getMemberEmail());

        TokenEntity saveToken;
        if(findToken == null) {
            TokenEntity tokenEntity = TokenEntity.changeEntity(tokenForOAuth2);
            tokenRepository.save(tokenEntity);
        } else {
            findToken.updateToken(tokenForOAuth2);
            tokenRepository.save(findToken);
        }

        // 유저에 대해 인증, 인가, 권한을 주기 위해서
        UserDetails userDetails = new User(memberEmail, "", authorities);
        log.info("userDetails : " + userDetails);
        // UserDetails 객체는 사용자의 주요 정보를 캡슐화하고
        // Authentication 객체는 사용자의 인증 상태와 권한을 나타냅니다
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, authorities);
        log.info("authentication : " + authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // attributes가 있는 생성자를 사용하여 PrincipalDetails 객체 생성
        // 소셜 로그인인 경우에는 attributes도 함께 가지고 있는 PrincipalDetails 객체를 생성하게 됩니다.
        PrincipalDetails principalDetails = new PrincipalDetails(findMember, oAuth2User.getAttributes());
        log.info("principalDetails : " + principalDetails);
        return principalDetails;
    }

    // 권한 가져오기 로직
    private List<GrantedAuthority> getAuthoritiesForUser(SocialMemberEntity findUser) {
        Auth role = findUser.getMemberRole();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        log.info("권한 : " + role.name());
        return authorities;
    }
}
