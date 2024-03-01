package com.example.shoppingmall02.member.repository;

import com.example.shoppingmall02.member.entity.SocialMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialMemberRepository extends JpaRepository<SocialMemberEntity, Long> {
    SocialMemberEntity findByMemberEmail(String email);
}
