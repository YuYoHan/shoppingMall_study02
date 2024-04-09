package com.example.shoppingmall02.jwt.controller;

import com.example.shoppingmall02.jwt.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tokens")
public class JwtController {
    private final TokenService tokenService;

    @GetMapping("")
    public ResponseEntity<?> requestAccessToken(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            ResponseEntity<?> accessToken = tokenService.createAccessToken(email);
            return ResponseEntity.ok().body(accessToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
