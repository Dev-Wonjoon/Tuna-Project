package net.tuna.member.service;

import lombok.RequiredArgsConstructor;
import net.tuna.member.dto.MemberDto;
import net.tuna.member.repository.MemberRepository;
import net.tuna.member.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    // username -> email
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        MemberDto member = memberRepository.findByEmail(username);

        if (member == null) {
            throw new UsernameNotFoundException("존재하지 않는 회원입니다.");
        }

        return new CustomUserDetails(member);
    }
}