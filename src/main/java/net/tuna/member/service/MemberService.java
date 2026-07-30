package net.tuna.member.service;

import lombok.RequiredArgsConstructor;
import net.tuna.member.dto.MemberDto;
import net.tuna.member.dto.RequestSignUpDto;
import net.tuna.member.dto.Role;
import net.tuna.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean hasEmail(String email) {
        if (memberRepository.findByEmail(email) == null) {
            return false;
        }
        return true;
    }

    public int save(RequestSignUpDto requestSignUpDto) {
        String[] imgList = new String[]{
                "/img/tuna-note-blurple.png",
                "/img/tuna-note-coral.png",
                "/img/tuna-note-gold.png",
                "/img/tuna-note-mint.png",
                "/img/tuna-note-pink.png",
                "/img/tuna-note-sky-blue.png"
        };
        return memberRepository.save(
                MemberDto.builder()
                        .email(requestSignUpDto.getEmail())
                        .password(passwordEncoder.encode(requestSignUpDto.getPassword()))
                        .name((requestSignUpDto.getName()))
                        .imageUrl(imgList[(int)(Math.random() * 6)])
                        .role(Role.USER)
                        .build()
        );
    }
}
