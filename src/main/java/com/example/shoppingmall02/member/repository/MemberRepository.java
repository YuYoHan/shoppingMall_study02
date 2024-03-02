package com.example.shoppingmall02.member.repository;

import com.example.shoppingmall02.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    MemberEntity findByMemberEmail(String email);
    MemberEntity findByMemberNickName(String nickName);
}
