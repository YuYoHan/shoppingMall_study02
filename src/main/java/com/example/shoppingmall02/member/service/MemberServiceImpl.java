package com.example.shoppingmall02.member.service;

import com.example.shoppingmall02.config.jwt.JwtProvider;
import com.example.shoppingmall02.exception.member.MemberException;
import com.example.shoppingmall02.jwt.repository.TokenRepository;
import com.example.shoppingmall02.member.domain.RequestMemberDTO;
import com.example.shoppingmall02.member.domain.ResponseMemberDTO;
import com.example.shoppingmall02.member.domain.UpdateMemberDTO;
import com.example.shoppingmall02.member.entity.MemberEntity;
import com.example.shoppingmall02.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

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
        return null;
    }

    @Override
    public ResponseEntity<?> updateUser(Long memberId, UpdateMemberDTO updateMemberDTO, String memberEmail) {
        return null;
    }


}
