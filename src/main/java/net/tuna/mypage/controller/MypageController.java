package net.tuna.mypage.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.tuna.member.dto.MemberDto;
import net.tuna.member.security.CustomUserDetails;
import net.tuna.member.service.MemberService;
import net.tuna.mypage.service.MypageService;
import net.tuna.post.dto.PostDto;
import net.tuna.post.service.PostService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MypageController {
    private final MypageService mypageService;
    private final MemberService memberService;
    private final PostService postService;

    @GetMapping("/mypage")
    public String mypage(
            @AuthenticationPrincipal CustomUserDetails user,
            Model model
    ) {
        MemberDto member = mypageService.getMypage(user);
        List<PostDto> posts = mypageService.getMyPosts(member.getId());

        model.addAttribute("member", member);
        model.addAttribute("posts", posts);

        return "pages/mypage";
    }

    @PostMapping("/members/delete")
    public String deleteMember(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        new SecurityContextLogoutHandler().logout(
                request,
                response,
                authentication
        );

        memberService.deleteById(userDetails.getMemberId());

        return "redirect:/";
    }
}
