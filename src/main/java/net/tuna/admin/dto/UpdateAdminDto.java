package net.tuna.admin.dto;

import lombok.Getter;
import lombok.Setter;
import net.tuna.member.dto.Role;

@Getter
@Setter
public class UpdateAdminDto {

    private Long id;
    private Role role;
}
