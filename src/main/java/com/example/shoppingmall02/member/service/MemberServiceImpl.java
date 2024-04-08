package com.example.shoppingmall02.member.service;

import com.example.shoppingmall02.config.jwt.JwtProvider;
import com.example.shoppingmall02.exception.member.MemberException;
import com.example.shoppingmall02.jwt.domain.ResponseTokenDTO;
import com.example.shoppingmall02.jwt.entity.TokenEntity;
import com.example.shoppingmall02.jwt.repository.TokenRepository;
import com.example.shoppingmall02.member.domain.RequestMemberDTO;
import com.example.shoppingmall02.member.domain.ResponseMemberDTO;
import com.example.shoppingmall02.member.domain.UpdateMemberDTO;
import com.example.shoppingmall02.member.entity.MemberEntity;
import com.example.shoppingmall02.member.repository.MemberRepository;
import com.example.shoppingmall02.member.role.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;

    @Override
    public boolean emailCheck(String email) {
        MemberEntity findMember = memberRepository.findByMemberEmail(email);
        return findMember == null;
    }

    @Override
    public boolean nickNameCheck(String nickName) {
        MemberEntity findMember = memberRepository.findByMemberNickName(nickName);
        return findMember == null;
    }

    // 회원 가입
    @Override
    public ResponseEntity<?> signUp(RequestMemberDTO requestMemberDTO) {
        String encode = passwordEncoder.encode(requestMemberDTO.getMemberPw());

        try {
            MemberEntity member = MemberEntity.save(requestMemberDTO, encode);
            MemberEntity saveMember = memberRepository.save(member);

            ResponseMemberDTO responseMemberDTO = ResponseMemberDTO.changeDTO(saveMember);
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Override
    public ResponseMemberDTO search(Long memberId) {
        try {
            MemberEntity findMember = memberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

            return ResponseMemberDTO.changeDTO(findMember);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    @Override
    public String removeUser(Long memberId, String email) {
        try {
            MemberEntity findMember = memberRepository.findByMemberEmail(email);

            if(findMember.getMemberId().equals(memberId)) {
                memberRepository.deleteById(memberId);
                return "회원 탈퇴 완료";
            } else {
                return "해당 유저가 아니라 삭제할 수 없습니다.";
            }
        } catch (Exception e) {
            throw new MemberException("회원 탈퇴하는데 실패했습니다.");
        }
    }

    @Override
    public ResponseEntity<?> login(String memberEmail, String memberPw) {
        try {
            // 회원 조회
            MemberEntity findMember = memberRepository.findByMemberEmail(memberEmail);

            if(findMember != null) {
                if(passwordEncoder.matches(memberPw, findMember.getMemberPw())) {
                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(memberEmail, memberPw);
                    List<GrantedAuthority> authorities = getAuthorities(findMember);

                    // JWT 생성
                    ResponseTokenDTO token = jwtProvider.createToken(authentication, authorities, findMember.getMemberId());
                    // JWT 조회
                    TokenEntity findToken = tokenRepository.findByMemberEmail(token.getMemberEmail());

                    // 토큰이 없다면 새로 발급
                    if(findToken == null) {
                        log.info("토큰이 없으므로 새롭게 발급합니다.");
                        findToken = TokenEntity.changeEntity(token);
                    } else {
                        log.info("기존에 발급한 토큰이 있으므로 업데이트합니다.");
                        findToken.updateToken(token);
                    }
                    tokenRepository.save(findToken);
                    return ResponseEntity.ok().body(token);
                }
            }
            throw new EntityNotFoundException("회원이 존재하지 않습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    // 토큰을 생성할 때 권한을 넣어주기 위해서
    private List<GrantedAuthority> getAuthorities(MemberEntity member) {
        Auth memberRole = member.getMemberRole();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + memberRole.name()));
        return authorities;
    }

    @Override
    public ResponseEntity<?> updateUser(Long memberId,
                                        UpdateMemberDTO updateMemberDTO,
                                        String memberEmail) {
        try {
            MemberEntity findMember = memberRepository.findByMemberEmail(memberEmail);

            // 닉네임 중복 체크
            if (!nickNameCheck(updateMemberDTO.getMemberNickName())) {
                throw new MemberException("이미 존재하는 닉네임이 있습니다.");
            }

            String encodePw = null;
            if(updateMemberDTO.getMemberPw() != null) {
                encodePw = passwordEncoder.encode(updateMemberDTO.getMemberPw());
            }

            if(findMember.getMemberId().equals(memberId)) {
                findMember.updateMember(updateMemberDTO, encodePw);

                MemberEntity update = memberRepository.save(findMember);
                ResponseMemberDTO responseMemberDTO = ResponseMemberDTO.changeDTO(update);
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                throw new MemberException("회원 정보가 일치하지 않습니다.");
            }
        }
    }


}
