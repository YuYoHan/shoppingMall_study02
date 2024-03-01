package com.example.shoppingmall02.member.entity;

import com.example.shoppingmall02.config.auditing.entity.BaseTimeEntity;
import com.example.shoppingmall02.member.role.Auth;
import lombok.*;

import javax.persistence.*;

@Entity(name = "social")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SocialMemberEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;
    @Column(name = "member_name", nullable = false)
    private String memberName;
    @Column(name = "member_pw", nullable = false)
    private String memberPw;

    @Column(name = "member_email", updatable = false, unique = true)
    private String memberEmail;
    @Column(name = "member_nickName", unique = true)
    private String memberNickName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Auth memberRole;
    private String provider;
    private String providerId;

    // 저장
    public static SocialMemberEntity save(String email,
                                          String name,
                                          Auth role,
                                          String nickName,
                                          String provider,
                                          String providerId) {
        return SocialMemberEntity.builder()
                .memberEmail(email)
                .memberName(name)
                .memberRole(role)
                .memberNickName(nickName)
                .provider(provider)
                .providerId(providerId)
                .build();
    }

    // 수정
    public void update(String nickName) {
        this.memberNickName = nickName;
    }
}
