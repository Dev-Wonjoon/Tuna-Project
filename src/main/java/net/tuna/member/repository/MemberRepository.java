package net.tuna.member.repository;

import net.tuna.member.dto.MemberDto;

import java.util.List;

public interface MemberRepository {
    MemberDto findByEmail(String email);
    int save(MemberDto memberDto);
    MemberDto findById(Long id);
    int updateRole(MemberDto memberDto);
    void deleteById(Long id);
}
