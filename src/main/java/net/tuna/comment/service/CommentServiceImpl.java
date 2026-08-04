package net.tuna.comment.service;

import lombok.RequiredArgsConstructor;
import net.tuna.comment.dto.CommentDto;
import net.tuna.comment.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;

    @Override
    public List<CommentDto> findByPostId(long postId) {
        return commentRepository.findByPostId(postId);
    }

    @Override
    public int writeComment(CommentDto comment) {
        return commentRepository.createComment(comment);
    }

    @Override
    public int editComment(CommentDto comment) {
        return commentRepository.updateComment(comment);
    }

    @Override
    public CommentDto findById(long id) {
        return commentRepository.findById(id);
    }

    @Override
    public int deleteById(long id) {
        return commentRepository.deleteById(id);
    }
}
