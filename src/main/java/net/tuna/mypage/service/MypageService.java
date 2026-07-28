package net.tuna.mypage.service;

import lombok.RequiredArgsConstructor;
import net.tuna.member.dto.MemberDto;
import net.tuna.member.repository.MemberRepository;
import net.tuna.member.security.CustomUserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final MemberRepository memberRepository;

    public MemberDto getMypage(CustomUserDetails user) {
        return memberRepository.findById(user.getMember().getId());
    }
}
