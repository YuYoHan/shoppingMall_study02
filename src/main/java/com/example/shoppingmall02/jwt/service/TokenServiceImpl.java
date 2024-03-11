package com.example.shoppingmall02.jwt.service;

import com.example.shoppingmall02.config.jwt.JwtProvider;
import com.example.shoppingmall02.jwt.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class TokenServiceImpl extends TokenService{
    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;
    private final
}
