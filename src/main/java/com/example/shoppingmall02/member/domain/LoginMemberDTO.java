package com.example.shoppingmall02.member.domain;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

// 로그인 시 받을 DTO
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginMemberDTO {
    @NotNull(message = "이메일은 필수 입력입니다.")
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String memberEmail;
    @NotNull(message = "비밀번호를 입력해주세요")
    private String memberPw;
}
