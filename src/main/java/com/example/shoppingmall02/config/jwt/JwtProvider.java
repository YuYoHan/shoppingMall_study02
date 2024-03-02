package com.example.shoppingmall02.config.jwt;

import com.example.shoppingmall02.jwt.domain.ResponseTokenDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class JwtProvider {
    private static final String AUTHORITIES_KEY = "auth";
    @Value("${jwt.access.expiration}")
    private long accessTokenTime;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenTime;

    private Key key;

    public JwtProvider(@Value("${jwt.secret_key}") String secret) {
        byte[] secretByte = DatatypeConverter.parseBase64Binary(secret);
        this.key = Keys.hmacShaKeyFor(secretByte);
    }

    // 토큰 생성
    public ResponseTokenDTO createToken(Authentication authentication,
                                        List<GrantedAuthority> authorities,
                                        Long memberId) {
        log.info("authentication : " + authentication);

        Map<String, Object> claims = new HashMap<>();
        List<String> auth = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put(AUTHORITIES_KEY, auth);
        claims.put("sub", authentication.getName());
        log.info("claims : " + claims);

        long now = (new Date()).getTime();
        Date now2 = new Date();

        Date accessTokenExpire = new Date(now + this.accessTokenTime);
        String accessToken = Jwts.builder()
                .setIssuedAt(now2)
                .setClaims(claims)
                .setExpiration(accessTokenExpire)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        Date refreshTokenExpire = new Date(now + this.refreshTokenTime);
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now2)
                .setExpiration(refreshTokenExpire)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return ResponseTokenDTO.builder()
                .grantType("Bearer ")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberEmail(authentication.getName())
                .memberId(memberId)
                .build();
    }

    public ResponseTokenDTO createTokenForOAuth2(String memberEmail,
                                                 List<GrantedAuthority> authorities,
                                                 Long memberId) {
        log.info("email in JwtProvicer : " + memberEmail);
        log.info("authorities in JwtProvicer : " + authorities);

        // 권한 가져오기
        Map<String, Object> claims = new HashMap<>();
        claims.put(AUTHORITIES_KEY, authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("sub", memberEmail);
        log.info("권한 JwtProvicer : " + claims);
        log.info("claims sub JwtProvicer : " + claims.get("sub"));

        long now = (new Date()).getTime();
        Date now2 = new Date();

        // accessToken 생성
        Date accessTokenExpire = new Date(now + this.accessTokenTime);
        String accessToken = Jwts.builder()
                .setIssuedAt(now2)
                .setClaims(claims)
                .setExpiration(accessTokenExpire)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        Date refreshTokenExpire = new Date(now + this.refreshTokenTime);
        String refreshToken = Jwts.builder()
                .setIssuedAt(now2)
                .setClaims(claims)
                .setExpiration(refreshTokenExpire)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return ResponseTokenDTO.builder()
                .grantType("Bearer ")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberEmail(memberEmail)
                .memberId(memberId)
                .build();
    }

    // acccessToken 발급
    public ResponseTokenDTO createAccessToken(String memberEmail,
                                              List<GrantedAuthority> authorities) {
        long now = (new Date()).getTime();
        Date now2 = new Date();
        Date accessTokenExpire = new Date(now + this.accessTokenTime);

        Map<String, Object> claims = new HashMap<>();
        List<String> auth = authorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put(AUTHORITIES_KEY, auth);
        claims.put("sub", memberEmail);

        String accessToken = Jwts.builder()
                .setIssuedAt(now2)
                .setClaims(claims)
                .setExpiration(accessTokenExpire)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return ResponseTokenDTO.builder()
                .grantType("Bearer ")
                .accessToken(accessToken)
                .memberEmail(memberEmail)
                .build();
    }


    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        List<String> auth = (List<String>) claims.get(AUTHORITIES_KEY);
        log.info("auth : " + auth);

        List<SimpleGrantedAuthority> authorities = auth
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        // 토큰에 인증, 인가, 권한을 주기위한 절차
        UserDetails userDetails = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("error : " + e.getMessage() +"\n, " + e.getClaims());
            return e.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 설명입니다. \n info : " + e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT입니다. \n info : " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT입니다. \n info : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT가 잘못되었습니다. \n info : " + e.getMessage());
        }
        return false;
    }
}
