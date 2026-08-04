package net.tuna.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.tuna.comment.dto.CommentDto;
import net.tuna.comment.service.CommentService;
import net.tuna.member.dto.Role;
import net.tuna.member.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{postId}/comments")
    public String writeComment(
            @PathVariable("postId") Long postId,
            @Valid @ModelAttribute("commentForm")CommentDto comment,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            return "redirect:/posts/" + postId + "#comments";
        }

        if (userDetails != null) {
            comment.setMemberId(userDetails.getMemberId());
        }
        comment.setPostId(postId);
        commentService.writeComment(comment);
        return "redirect:/posts/" + postId + "#comments";
    }

    @PostMapping("/{postId}/comments/{commentId}/edit")
    public String editComment(
            @PathVariable("postId") long postId,
            @ModelAttribute("commentForm") CommentDto comment,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long commentMemberId = commentService.findById(comment.getId()).getMemberId();
        Long memberId = userDetails.getMemberId();

        // 일단 조건에 맞으면 수정 동작을 하게 짰는데, 왠만하면 조건 안되면 에러페이지를 띄우고 싶다.
        if (commentMemberId.equals(memberId) || userDetails.getRole() == Role.ADMIN) {
            commentService.editComment(comment);
        }

        return "redirect:/posts/" + postId + "#comments";
    }

    @PostMapping("/{postId}/comments/{commentId}/delete")
    public String deleteComment(
            @PathVariable("postId") long postId,
            @PathVariable("commentId") long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long commentMemberId = commentService.findById(commentId).getMemberId();
        Long memberId = userDetails.getMemberId();

        // 일단 조건에 맞으면 삭제 동작을 하게 짰는데, 왠만하면 조건 안되면 에러페이지를 띄우고 싶다.
        if (commentMemberId.equals(memberId) || userDetails.getRole() == Role.ADMIN) {
            commentService.deleteById(commentId);
        }

        return "redirect:/posts/" + postId + "#comments";
    }
}
