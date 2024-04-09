package com.example.shoppingmall02.jwt.service;

import org.springframework.http.ResponseEntity;

public interface TokenService {
    ResponseEntity<?> createAccessToken(String email);
}
