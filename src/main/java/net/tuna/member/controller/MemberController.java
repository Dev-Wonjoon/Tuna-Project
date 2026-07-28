package net.tuna.member.controller;

import lombok.RequiredArgsConstructor;
import net.tuna.member.dto.RequestSignUpDto;
import net.tuna.member.service.MemberService;
import org.springframework.stereotype.Controller;
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
    public String signupPage() {
        return "pages/auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("signUpForm") RequestSignUpDto requestSignUpDto) {
        memberService.save(requestSignUpDto);
        return "redirect:/login";
    }
}
