package net.tuna.admin.controller;

import lombok.RequiredArgsConstructor;
import net.tuna.admin.dto.RequestAdminCreateDto;
import net.tuna.admin.dto.UpdateAdminDto;
import net.tuna.admin.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<String> createAdmin(
            @RequestBody RequestAdminCreateDto requestAdminCreateDto
    ) {

        adminService.save(requestAdminCreateDto);

        return ResponseEntity.ok("관리자 계정 생성 완료");
    }

    @PostMapping("/update-role")
    public String updateAdmin(UpdateAdminDto updateAdminDto
    ) {
        adminService.updateAdmin(updateAdminDto);

        return "redirect:/admin";
    }

    @GetMapping
    public String adminPage(Model model) {

        model.addAttribute("members", adminService.getAllMembers());

        return "pages/admin";
    }

    @PostMapping("/members/{id}/delete")
    public String deleteMember(@PathVariable Long id) {

        adminService.deleteMember(id);

        return "redirect:/admin";
    }
}
