package net.tuna.post.service;

import net.tuna.post.dto.PostDto;
import net.tuna.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    @Override
    public void writePost(PostDto post) {
        postRepository.createPost(post);
    }

    @Override
    public PostDto getPost(long id) {
        return  postRepository.findById(id);
    }

    @Override
    public List<Map<String, Object>> getComments(long id) {
        return postRepository.findCommentsById(id);
    }

    @Override
    public List<PostDto> getPostsByMemberId(long id) {
        return postRepository.findPostsByMemberId(id);
    }

    @Override
    public void deletePost(long id) {
        postRepository.deleteById(id);
    }

    @Override
    public void addViewCount(long id) {
        postRepository.addViewCount(id);
    }
}
