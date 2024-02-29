package com.example.shoppingmall02.config.oauth;

import com.example.shoppingmall02.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class PrincipalOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final SocialMemberRepository socialMemberRepository;
    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;
}
