package com.example.shoppingmall02.jwt.domain;

import com.example.shoppingmall02.jwt.entity.TokenEntity;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseTokenDTO {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String memberEmail;
    private Long memberId;

    // 엔티티를 DTO로 변환
    public static ResponseTokenDTO changeDTO(TokenEntity token) {
        return ResponseTokenDTO.builder()
                .grantType(token.getGrantType())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .memberEmail(token.getMemberEmail())
                .memberId(token.getMemberId())
                .build();
    }
}
