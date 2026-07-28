package net.tuna.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.tuna.member.dto.RequestSignUpDto;
import net.tuna.member.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
            @Valid @ModelAttribute("signUpForm") RequestSignUpDto requestSignUpDto,
            BindingResult bindingResult
    ) {
        if (!requestSignUpDto.getPassword().equals(requestSignUpDto.getPasswordConfirm())) {
            bindingResult.rejectValue(
                    "passwordConfirm",
                    "password_not_matched",
                    "비밀번호 확인이 맞지 않습니다."
            );
        }

        if (bindingResult.hasErrors()) {
            return "pages/auth/signup";
        }

        memberService.save(requestSignUpDto);
        return "redirect:/login";
    }
}
