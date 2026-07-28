package net.tuna.post.repository;

import net.tuna.post.dto.PostDto;

import java.util.List;
import java.util.Map;

public interface PostRepository {
    List<PostDto> findAll();
    void createPost(PostDto post);
    PostDto findById(long id);
    List<Map<String, Object>> findCommentsById(long id);
}
