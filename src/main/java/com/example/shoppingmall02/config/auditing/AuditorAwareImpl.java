package com.example.shoppingmall02.config.auditing;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // SecurityContextHodler에  저장된 값을 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = "";
        if(authentication != null) {
            // 여기서 getName은 principalDetails에 이메일로 설정하면 그걸 가져온다.
            email = authentication.getName();
        }
        return Optional.of(email);
    }
}
