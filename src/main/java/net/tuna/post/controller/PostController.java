package net.tuna.mock.post.controller;

import lombok.extern.slf4j.Slf4j;
import net.tuna.member.security.CustomUserDetails;
import net.tuna.mock.post.dto.PostDto;
import net.tuna.mock.post.service.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

@Controller
@Slf4j
@RequestMapping("/")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String getPostList(Model model, @AuthenticationPrincipal CustomUserDetails userDetails){

        if(userDetails != null){
            String email = userDetails.getMember().getEmail();
            model.addAttribute("authorEmail",email);
        }
        List<PostDto> posts = postService.getPosts();
        model.addAttribute("posts", posts );
        return "pages/home";
    }
}
