package com.example.shoppingmall02.config.sercurity;

import com.example.shoppingmall02.member.entity.MemberEntity;
import com.example.shoppingmall02.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberEntity findMember = memberRepository.findByMemberEmail(username);
        log.info("member : " + findMember);

        if(findMember == null) {
            throw new UsernameNotFoundException("해당 사용자가 없습니다.");
        } else {
            return new PrincipalDetails(findMember);
        }
    }
}
