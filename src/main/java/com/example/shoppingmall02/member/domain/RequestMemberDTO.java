package com.example.shoppingmall02.member.domain;

import com.example.shoppingmall02.member.role.Auth;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

// 회원가입 시 프론트에게 받을 DTO
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestMemberDTO {
    @NotNull(message = "이메일은 필 수 입니다.")
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String memberEmail;

    @NotNull(message = "이름은 필 수 입니다.")
    private String memberName;

    @NotNull(message = "닉네임은 필수 입력입니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣]*$", message = "사용자이름은 영어와 한글만 가능합니다.")
    private String memberNickName;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,20}",
            message = "비밀번호는 영문 소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8 ~20자의 비밀번호여야 합니다.")
    private String memberPw;

    @NotNull(message = "권한정보는 필수 입력입니다.")
    private Auth memberRole;

    private AddressDTO memberAddress;
}
