package net.tuna.admin.service;

import lombok.RequiredArgsConstructor;
import net.tuna.admin.dto.RequestAdminCreateDto;
import net.tuna.admin.dto.UpdateAdminDto;
import net.tuna.member.dto.MemberDto;
import net.tuna.member.dto.Role;
import net.tuna.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public int save(RequestAdminCreateDto requestAdminCreateDto) {
        return memberRepository.saveAdmin(
                MemberDto.builder()
                        .email(requestAdminCreateDto.getEmail())
                        .password(passwordEncoder.encode(requestAdminCreateDto.getPassword()))
                        .role(Role.ADMIN)
                        .build()
        );
    }

    public void updateAdmin(UpdateAdminDto updateAdminDto) {

        MemberDto member = memberRepository.findById(updateAdminDto.getId());

        if (member == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        member.setRole(updateAdminDto.getRole());

        memberRepository.updateRole(member);
    }
}