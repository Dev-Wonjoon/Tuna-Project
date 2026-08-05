package net.tuna.admin.controller;

import lombok.RequiredArgsConstructor;
import net.tuna.admin.dto.RequestAdminCreateDto;
import net.tuna.admin.dto.UpdateAdminDto;
import net.tuna.admin.service.AdminService;
import net.tuna.member.dto.RequestSignUpDto;
import net.tuna.member.dto.Role;
import net.tuna.member.service.MemberService;
import net.tuna.member.validation.ValidationSequence;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final MemberService memberService;

    @GetMapping("/signup")
    public String adminSignupPage(@ModelAttribute("signUpForm") RequestSignUpDto requestSignUpDto) {
        return "pages/auth/admin-signup";
    }

    @PostMapping("/signup")
    public String createAdmin(
            @Validated(ValidationSequence.class) @ModelAttribute("signUpForm") RequestSignUpDto requestSignUpDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (!requestSignUpDto.getPassword().equals(requestSignUpDto.getPasswordConfirm())) {
            bindingResult.rejectValue(
                    "passwordConfirm",
                    "password_not_matched",
                    "비밀번호 확인이 맞지 않습니다."
            );
        }

        if (memberService.hasEmail(requestSignUpDto.getEmail())) {
            bindingResult.rejectValue(
                    "email",
                    "email_already_exists",
                    "해당 이메일은 이미 존재합니다."
            );
        }

        if (bindingResult.hasErrors()) {
            return "pages/auth/admin-signup";
        }

        memberService.save(requestSignUpDto, Role.ADMIN);
        redirectAttributes.addFlashAttribute("message", "관리자 계정 생성이 완료되었습니다.");

        return "redirect:/login";
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
