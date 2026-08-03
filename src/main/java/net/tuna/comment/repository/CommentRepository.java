package net.tuna.comment.repository;

import net.tuna.comment.dto.CommentDto;

import java.util.List;

public interface CommentRepository {
    List<CommentDto> findByPostId(Long postId);
    int createComment(CommentDto comment);
    int updateComment(CommentDto comment);
    CommentDto findById(Long id);
    int deleteById(Long id);
}
