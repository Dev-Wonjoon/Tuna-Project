package net.tuna.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.tuna.member.dto.Role;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminMemberDto {

    private Long id;

    private String email;

    private String name;

    private Role role;

    private LocalDateTime createdAt;

    private int postCount;
}
