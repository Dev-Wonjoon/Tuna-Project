package net.tuna.admin.controller;

import lombok.RequiredArgsConstructor;
import net.tuna.admin.dto.RequestAdminCreateDto;
import net.tuna.admin.dto.UpdateAdminDto;
import net.tuna.admin.service.AdminService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/signup")
    public void createAdmin(
            @RequestBody RequestAdminCreateDto requestAdminCreateDto
    ) {
        adminService.save(requestAdminCreateDto);
    }

    @PostMapping("/update-role")
    public void updateAdmin(
            @RequestBody UpdateAdminDto updateAdminDto
    ) {
        adminService.updateAdmin(updateAdminDto);
    }
}
