package com.example.shoppingmall02.config.oauth;

import com.example.shoppingmall02.jwt.domain.ResponseTokenDTO;
import com.example.shoppingmall02.jwt.entity.TokenEntity;
import com.example.shoppingmall02.jwt.repository.TokenRepository;
import com.example.shoppingmall02.member.domain.SocialResponseDTO;
import com.example.shoppingmall02.member.entity.SocialMemberEntity;
import com.example.shoppingmall02.member.repository.MemberRepository;
import com.example.shoppingmall02.member.repository.SocialMemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final SocialMemberRepository socialMemberRepository;
    private final TokenRepository tokenRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            log.info("소셜 로그인 성공");
            // 소셜 로그인 이메일 가져오기
            String email = authentication.getName();
            log.info("이메일 : " + email);
            TokenEntity findToken = tokenRepository.findByMemberEmail(email);
            ResponseTokenDTO responseTokenDTO = ResponseTokenDTO.changeDTO(findToken);
            log.info("토큰 : " + responseTokenDTO);
            SocialMemberEntity findMember = socialMemberRepository.findByMemberEmail(email);
            SocialResponseDTO socialResponseDTO = SocialResponseDTO.changeDTO(findMember);
            log.info("소셜 로그인 유저 : " + socialResponseDTO);

            // HTTP 바디에 담아준다.
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("소셜로그인 유저 정보", socialResponseDTO);
            responseBody.put("토큰 정보", responseTokenDTO);

            // JSON 응답 전송
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("정보를 찾아오지 못했습니다.");
            response.getWriter().write("OAuth2 로그인 성공 후 오류 발생 : " + e.getMessage());
            // 이 메서드는 버퍼에 있는 내용을 클라이언트에게 보냅니다.
            // 데이터를 작성하고 나서는 flush()를 호출하여 실제로 데이터를 클라이언트로 전송합니다.
            response.getWriter().flush();
        }
    }
}
