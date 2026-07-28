package net.tuna.post.service;

import net.tuna.post.dto.PostDto;

import java.util.List;

public interface PostService {
    List<PostDto> getPosts();
    void writePost(PostDto post);
}
