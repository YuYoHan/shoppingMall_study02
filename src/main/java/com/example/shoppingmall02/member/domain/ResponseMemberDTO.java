package com.example.shoppingmall02.member.domain;

import com.example.shoppingmall02.member.entity.MemberEntity;
import com.example.shoppingmall02.member.role.Auth;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

// 일반 로그인 시 반환할 DTO
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseMemberDTO {
    private Long memberId;

    @NotNull(message = "이메일은 필수 입력입니다.")
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotNull(message = "이름은 필수 입력입니다.")
    private String memberName;

    @NotNull(message = "닉네임은 필수 입력입니다.")
    private String memberNickName;

    @NotNull(message = "권한정보는 필수 입력입니다.")
    private Auth memberRole;

    private AddressDTO memberAddress;

    // DTO로 변환
    public static  ResponseMemberDTO changeDTO(MemberEntity member) {
        return ResponseMemberDTO.builder()
                .memberId(member.getMemberId())
                .email(member.getMemberEmail())
                .memberNickName(member.getMemberNickName())
                .memberRole(member.getMemberRole())
                .memberAddress(AddressDTO.builder()
                        .memberAddr(member.getAddress().getMemberAddr() == null
                        ? null : member.getAddress().getMemberAddr())
                        .memberAddrDetail(member.getAddress().getMemberAddrDetail() == null
                        ? null : member.getAddress().getMemberAddrDetail())
                        .memberZipCode(member.getAddress().getMemberZipCode() == null
                        ? null : member.getAddress().getMemberZipCode())
                        .build())
                .build();
    }
}
