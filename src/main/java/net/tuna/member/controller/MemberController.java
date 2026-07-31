package net.tuna.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.tuna.member.dto.RequestSignUpDto;
import net.tuna.member.service.MemberService;
import net.tuna.member.validation.ValidationSequence;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String login() {

        return "pages/auth/login"; //url 알맞게 수정
    }

    @GetMapping("/signup")
    public String signupPage(@ModelAttribute("signUpForm") RequestSignUpDto requestSignUpDto) {
        return "pages/auth/signup";
    }

    @PostMapping("/signup")
    public String signup(
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
            return "pages/auth/signup";
        }

        memberService.save(requestSignUpDto);
        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");

        return "redirect:/login";
    }
}
