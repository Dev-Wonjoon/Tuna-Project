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
    public long writePost(PostDto post) {
        return postRepository.createPost(post);
    }

    @Override
    public void editPost(PostDto post) {
        postRepository.updatePost(post);
    }

    @Override
    public PostDto getPost(long id) {
        return  postRepository.findById(id);
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

    @Override
    public List<PostDto> getSearchPosts(String type, String value) {
        if (type.equals("searchTitleContent")) {
            return postRepository.findByKeywordFromTitleContent(value);
        }
        if (type.equals("searchTitle")) {
            return postRepository.findByKeywordFromTitle(value);
        }
        if (type.equals("searchContent")) {
            return postRepository.findByKeywordFromContent(value);
        }
        if (type.equals("searchAuthor")) {
            return postRepository.findByKeywordFromAuthor(value);
        }
        return null;
    }
}
