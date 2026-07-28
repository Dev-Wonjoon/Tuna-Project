package net.tuna.mypage.controller;

import lombok.RequiredArgsConstructor;
import net.tuna.member.dto.MemberDto;
import net.tuna.member.security.CustomUserDetails;
import net.tuna.mypage.service.MypageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MypageController {
    private final MypageService mypageService;

    @GetMapping("/mypage")
    public String mypage(
            @AuthenticationPrincipal CustomUserDetails user,
            Model model
    ) {
        MemberDto member = mypageService.getMypage(user);

        model.addAttribute("member", member);

        return "pages/mypage";
    }
}
