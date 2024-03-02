package com.example.shoppingmall02.member.service;

import com.example.shoppingmall02.config.jwt.JwtProvider;
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
        return null;
    }

    @Override
    public ResponseMemberDTO search(Long memberId) {
        return null;
    }

    @Override
    public String removeUser(Long memberId, String email) {
        return null;
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
