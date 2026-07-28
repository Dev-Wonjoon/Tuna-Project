package net.tuna.member.service;

import lombok.RequiredArgsConstructor;
import net.tuna.member.dto.MemberDto;
import net.tuna.member.dto.RequestSignUpDto;
import net.tuna.member.dto.Role;
import net.tuna.member.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public int save(RequestSignUpDto requestSignUpDto) {
        return memberRepository.save(
                MemberDto.builder()
                        .email(requestSignUpDto.getEmail())
                        .password(passwordEncoder.encode(requestSignUpDto.getPassword()))
                        .role(Role.USER)
                        .build()
        );
    }
}
