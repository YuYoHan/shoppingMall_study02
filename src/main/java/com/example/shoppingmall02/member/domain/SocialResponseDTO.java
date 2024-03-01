package com.example.shoppingmall02.member.domain;

import com.example.shoppingmall02.member.entity.SocialMemberEntity;
import com.example.shoppingmall02.member.role.Auth;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialResponseDTO {
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

    private String provider;
    private String providerId;


    public static SocialResponseDTO changeDTO(SocialMemberEntity member) {
        return SocialResponseDTO.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .memberNickName(member.getMemberNickName())
                .provider(member.getProvider())
                .providerId(member.getProviderId())
                .memberRole(member.getMemberRole())
                .build();
    }
}
