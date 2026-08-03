package net.tuna.comment.repository;

import net.tuna.comment.dto.CommentDto;

import java.util.List;

public interface CommentRepository {
    List<CommentDto> findByPostId(long postId);
    int createComment(CommentDto comment);
    int updateComment(CommentDto comment);
    CommentDto findById(long id);
    int deleteById(long id);
}
