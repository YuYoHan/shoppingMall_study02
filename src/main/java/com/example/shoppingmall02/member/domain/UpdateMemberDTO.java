package com.example.shoppingmall02.member.domain;

import lombok.*;

import javax.validation.constraints.Pattern;

// 수정 시 사용할 DTO
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberDTO {
    @Pattern(regexp = "^[a-zA-Z가-힣]*$", message = "사용자이름은 영어와 한글만 가능합니다.")
    private String memberNickName;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,15}",
            message = "비밀번호는 영문 소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8 ~15자의 비밀번호여야 합니다." )
    private String memberPw;

    private AddressDTO memberAddress;
}
