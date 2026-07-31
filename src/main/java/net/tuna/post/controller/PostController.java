package net.tuna.post.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.tuna.member.security.CustomUserDetails;
import net.tuna.post.dto.PostDto;
import net.tuna.post.service.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
@Slf4j
@RequestMapping("/")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String getPostList(Model model){
        List<PostDto> posts = postService.getPosts();
        model.addAttribute("posts", posts );
        return "pages/home";
    }

    //게시글 등록화면 요청
    @GetMapping("/posts/new")
    public String getCreateForm(@ModelAttribute("postForm") PostDto post){
        return "pages/post-create";
    }

    //게시글 등록 요청
    @PostMapping("/posts")
    public String createPost(@Valid @ModelAttribute("postForm") PostDto post,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal CustomUserDetails userDetails){
        if(bindingResult.hasFieldErrors()){
            return "pages/post-create";
        }
        //멤버단에서 본인 아이디가져오기
        if(userDetails != null){
            post.setMemberId(userDetails.getMember().getId());
        }

        postService.writePost(post);
        return "redirect:/";
    }

    //게시글 수정화면 요청
    @GetMapping("/posts/{postId}/edit")
    public String getEditForm(@PathVariable("postId") long id,
                              Model model){
        PostDto post = postService.getPost(id);
        model.addAttribute("post",post);
        return "pages/post-edit";
    }

    // 게시글 수정 요청
    @PostMapping("/posts/{postId}/edit")
    public String eidtPost(@PathVariable("postId") long id,
                           @ModelAttribute("postForm") PostDto post){
        post.setId(id);
        postService.editPost(post);
        return "redirect:/posts/"+id;
    }

    @GetMapping("/posts/{postId}")
    public String getDetail(@PathVariable("postId") long id, Model model
            ,@AuthenticationPrincipal CustomUserDetails userDetails){
        postService.addViewCount(id);
        PostDto post = postService.getPost(id);
        model.addAttribute("post",post);
        //작성자 본인 검증
        boolean isAuthor = false;
        if (userDetails != null && post != null) {
            String name = userDetails.getMember().getName();
            model.addAttribute("name", name);

            isAuthor = java.util.Objects.equals(
                    userDetails.getMember().getId(),
                    post.getMemberId()
            );
        }
        model.addAttribute("isAuthor", isAuthor);
        List<Map<String,Object>> comments = postService.getComments(id);
        model.addAttribute("comments",comments);

        return "pages/post-detail";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable("id") long id,
                             Model model,
                             @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.deletePost(id);
        return "redirect:/";
    }

    @GetMapping("/search")
    public String searchPosts(
            @RequestParam("searchType") String type,
            @RequestParam("keyword") String keyword,
            Model model
    ) {
        List<PostDto> posts = postService.getSearchPosts(type, keyword);
        model.addAttribute("posts", posts);
        return "pages/home";
    }

}
