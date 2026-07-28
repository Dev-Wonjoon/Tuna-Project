package net.tuna.post.repository;

import net.tuna.post.dto.PostDto;

import java.util.List;

public interface PostRepository {
    List<PostDto> findAll();
    void createPost(PostDto post);

}
