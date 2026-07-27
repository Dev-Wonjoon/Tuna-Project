package net.tuna.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

    @GetMapping("/login")
    public String login() {

        return "pages/auth/login"; //url 알맞게 수정
    }
}
