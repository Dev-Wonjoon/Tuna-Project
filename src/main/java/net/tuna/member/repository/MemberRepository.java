package net.tuna.member.repository;

import net.tuna.member.dto.MemberDto;

import java.util.List;

public interface MemberRepository {
    MemberDto findByEmail(String email);
}
