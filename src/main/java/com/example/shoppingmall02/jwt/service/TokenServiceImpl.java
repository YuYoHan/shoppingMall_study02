package com.example.shoppingmall02.jwt.service;

import com.example.shoppingmall02.config.jwt.JwtProvider;
import com.example.shoppingmall02.jwt.domain.ResponseTokenDTO;
import com.example.shoppingmall02.jwt.entity.TokenEntity;
import com.example.shoppingmall02.jwt.repository.TokenRepository;
import com.example.shoppingmall02.member.entity.MemberEntity;
import com.example.shoppingmall02.member.repository.MemberRepository;
import com.example.shoppingmall02.member.role.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class TokenServiceImpl implements TokenService{
    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    public ResponseEntity<?> createAccessToken(String email) {
        try {
            // 토큰 조회
            TokenEntity findToken = tokenRepository.findByMemberEmail(email);
            // 유저 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(email);

            // 권한
            List<GrantedAuthority> authorities = getAuthorities(findMember);
            ResponseTokenDTO accessToken = jwtProvider.createAccessToken(findMember.getMemberEmail(), authorities);
            findToken.updateToken(accessToken);
            TokenEntity save = tokenRepository.save(findToken);
            ResponseTokenDTO responseTokenDTO = ResponseTokenDTO.changeDTO(save);
            return ResponseEntity.ok().body(responseTokenDTO);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 주어진 사용자에 대한 권한 정보를 가져오는 로직을 구현하는 메서드입니다.
    // 이 메서드는 데이터베이스나 다른 저장소에서 사용자의 권한 정보를 조회하고,
    // 해당 권한 정보를 List<GrantedAuthority> 형태로 반환합니다.
    private List<GrantedAuthority> getAuthorities(MemberEntity member) {
        Auth memberRole = member.getMemberRole();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + memberRole.name()));
        log.info("권한 : " + authorities);
        return authorities;
    }
}
