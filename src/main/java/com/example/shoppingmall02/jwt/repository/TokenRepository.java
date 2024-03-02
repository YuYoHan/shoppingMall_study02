package com.example.shoppingmall02.jwt.repository;

import com.example.shoppingmall02.jwt.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    TokenEntity findByMemberEmail(String memberEmail);
}
