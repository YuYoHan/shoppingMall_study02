package com.example.shoppingmall02.jwt.entity;

import com.example.shoppingmall02.config.auditing.entity.BaseTimeEntity;
import com.example.shoppingmall02.jwt.domain.ResponseTokenDTO;
import lombok.*;

import javax.persistence.*;

@Entity(name = "token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@AllArgsConstructor
@Builder
public class TokenEntity extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String memberEmail;
    private Long memberId;

    // 엔티티로 변환
    public static TokenEntity changeEntity(ResponseTokenDTO token) {
        return TokenEntity.builder()
                .grantType(token.getGrantType())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .memberEmail(token.getMemberEmail())
                .memberId(token.getMemberId())
                .build();
    }

    // 토큰 업데이트
    public void updateToken(ResponseTokenDTO token) {
        this.accessToken = token.getAccessToken();
    }
}
