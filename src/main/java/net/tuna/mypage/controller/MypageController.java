package net.tuna.mypage.controller;

import lombok.RequiredArgsConstructor;
import net.tuna.member.dto.MemberDto;
import net.tuna.member.security.CustomUserDetails;
import net.tuna.mypage.service.MypageService;
import net.tuna.post.dto.PostDto;
import net.tuna.post.service.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MypageController {
    private final MypageService mypageService;
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
}
