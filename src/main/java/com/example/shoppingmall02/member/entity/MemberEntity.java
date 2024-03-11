package com.example.shoppingmall02.member.entity;

import com.example.shoppingmall02.config.auditing.entity.BaseEntity;
import com.example.shoppingmall02.member.domain.AddressDTO;
import com.example.shoppingmall02.member.domain.RequestMemberDTO;
import com.example.shoppingmall02.member.domain.ResponseMemberDTO;
import com.example.shoppingmall02.member.role.Auth;
import lombok.*;

import javax.persistence.*;

@Entity(name = "member")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Embedded
    private AddressEntity address;

    // 저장
    public static MemberEntity save(RequestMemberDTO member, String encode) {
        return MemberEntity.builder()
                .memberEmail(member.getMemberEmail())
                .memberPw(encode)
                .memberName(member.getMemberName())
                .memberNickName(member.getMemberNickName())
                .memberRole(member.getMemberRole())
                .address(AddressEntity.builder()
                        .memberAddr(member.getMemberAddress().getMemberAddr() == null
                                ? null : member.getMemberAddress().getMemberAddr())
                        .memberAddrDetail(member.getMemberAddress().getMemberAddrDetail() == null
                                ? null : member.getMemberAddress().getMemberAddrDetail())
                        .memberZipCode(member.getMemberAddress().getMemberZipCode() == null
                                ? null : member.getMemberAddress().getMemberZipCode())
                        .build())
                .build();
    }

}
