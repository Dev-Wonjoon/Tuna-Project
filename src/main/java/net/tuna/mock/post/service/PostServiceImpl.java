package net.tuna.mock.post.service;

import net.tuna.mock.post.dto.PostDto;
import net.tuna.mock.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    public PostServiceImpl(@Qualifier("jdbcTemplatePostRepository")PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<PostDto> getPosts() {
        return postRepository.findAll();
    }
}
