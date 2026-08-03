package net.tuna.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.tuna.comment.dto.CommentDto;
import net.tuna.comment.service.CommentService;
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
            comment.setMemberId(userDetails.getMember().getId());
        }
        comment.setPostId(postId);
        commentService.writeComment(comment);
        return "redirect:/posts/" + postId + "#comments";
    }
}
