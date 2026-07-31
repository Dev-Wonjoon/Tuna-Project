package net.tuna.admin.repository;

import net.tuna.admin.dto.AdminMemberDto;

import java.util.List;

public interface AdminRepository {
    List<AdminMemberDto> findAllMembers();

    void deleteById(Long id);
}
