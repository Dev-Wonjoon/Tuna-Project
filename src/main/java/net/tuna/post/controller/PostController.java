package net.tuna.post.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.tuna.cursor.CursorSlice;
import net.tuna.comment.dto.CommentDto;
import net.tuna.comment.service.CommentService;
import net.tuna.member.dto.Role;
import net.tuna.member.security.CustomUserDetails;
import net.tuna.post.dto.PostDetailResponse;
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
    private static final String DEFAULT_PAGE_SIZE = "10";

    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping("/")
    public String getPostList(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            Model model
    ) {
        CursorSlice<PostDetailResponse> postSlice =
                postService.getPostSlice(cursor, size).map(PostDetailResponse::from);

        model.addAttribute("posts", postSlice.getContent());
        model.addAttribute("postNextCursor", postSlice.getNextCursor());
        model.addAttribute("pageSize", size);

        return "pages/home";
    }

    @GetMapping("/posts/page")
    public String getPostPage(
            @RequestParam String cursor,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE)
            int size,
            Model model
    ) {
        CursorSlice<PostDetailResponse> postSlice =
                postService.getPostSlice(cursor, size)
                        .map(PostDetailResponse::from);

        model.addAttribute("posts", postSlice.getContent());
        model.addAttribute("postNextCursor", postSlice.getNextCursor());

        return "fragments/post-page :: postPage";
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

        //멤버에서 로그인된 유저 ID 아이디가져오기
        if(userDetails != null){
            post.setMemberId(userDetails.getMemberId());
        }

        long redirectId = postService.writePost(post);
        return "redirect:/posts/" + redirectId;
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


    //게시글 상세보기
    @GetMapping("/posts/{postId}")
    public String getDetail(@PathVariable("postId") long id, Model model
            ,@AuthenticationPrincipal() CustomUserDetails userDetails){
        postService.addViewCount(id);
        PostDto post = postService.getPost(id);

        //시간정보가공
        PostDetailResponse response = PostDetailResponse.from(post);
        model.addAttribute("post",response);

        // 댓글 조회
        List<CommentDto> comments = commentService.findByPostId(id);
        model.addAttribute("comments",comments);

        return "pages/post-detail";
    }



    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable("id") long id){
        postService.deletePost(id);
        return "redirect:/";
    }

    @GetMapping("/search")
    public String searchPosts(
            @RequestParam("searchType") String type,
            @RequestParam("keyword") String keyword,
            Model model
    ) {
        List<PostDto> postDtos = postService.getSearchPosts(type, keyword);
        //시간정보가공
        List<PostDetailResponse> posts = postDtos.stream()
                .map(PostDetailResponse::from).toList();
        model.addAttribute("posts", posts);
        return "pages/home";
    }

}
