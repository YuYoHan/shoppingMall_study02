package com.example.shoppingmall02.member.controller;

import com.example.shoppingmall02.exception.validation.DataValidationException;
import com.example.shoppingmall02.member.domain.LoginMemberDTO;
import com.example.shoppingmall02.member.domain.RequestMemberDTO;
import com.example.shoppingmall02.member.domain.ResponseMemberDTO;
import com.example.shoppingmall02.member.domain.UpdateMemberDTO;
import com.example.shoppingmall02.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class MemberController {
    private final MemberService memberService;

    // 회원가입
    @PostMapping("")
    public ResponseEntity<?> join(@Validated @RequestBody RequestMemberDTO member,
                                  BindingResult result) {
        try {
            // 검증 예외가 발생하면 예외 메시지를 호출
            if(result.hasErrors()) {
                log.error("검증 예외 발생 : " + result.getClass().getSimpleName());
                throw new DataValidationException(result.getClass().getSimpleName());
            }

            ResponseEntity<?> responseEntity = memberService.signUp(member);
            return ResponseEntity.ok().body(responseEntity);
        } catch (Exception e) {
            log.error("예외 발생 : " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원조회
    @GetMapping("/{memberId}")
    public ResponseEntity<?> search(@PathVariable Long memberId) {
        try {
            ResponseMemberDTO search = memberService.search(memberId);
            return ResponseEntity.ok().body(search);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 회원탈퇴
    @DeleteMapping("/{memberId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> remove(@PathVariable Long memberId,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 인증받은 유저 정보에서 이메일 정보를 가져옵니다.
            String email = userDetails.getUsername();
            log.info("이메일 : " + email);
            String removeUser = memberService.removeUser(memberId, email);
            return ResponseEntity.ok().body(removeUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginMemberDTO login) {
        try {
            String memberEmail = login.getMemberEmail();
            String memberPw = login.getMemberPw();
            ResponseEntity<?> response = memberService.login(memberEmail, memberPw);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 회원수정
    @PutMapping("/{memberId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> update(@PathVariable Long memberId,
                                    @Validated @RequestBody UpdateMemberDTO member,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            log.info("수정할 유저 정보 : " + member);
            ResponseEntity<?> responseEntity = memberService.updateUser(memberId, member, email);
            return ResponseEntity.ok().body(responseEntity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 이메일 중복체크
    @GetMapping("/email/{memberEmail}")
    public boolean emailCheck(@PathVariable String memberEmail) {
        return memberService.emailCheck(memberEmail);
    }

    // 닉네임 중복체크
    @GetMapping("/nickName/{nickName}")
    public boolean nickNameCheck(@PathVariable String nickName) {
        return memberService.nickNameCheck(nickName);
    }


}
