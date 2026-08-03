package net.tuna.comment.service;

import net.tuna.comment.dto.CommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> findByPostId(Long postId);
    int writeComment(CommentDto comment);
    int editComment(CommentDto comment);
    CommentDto findById(Long id);
    int deleteById(Long id);
}
