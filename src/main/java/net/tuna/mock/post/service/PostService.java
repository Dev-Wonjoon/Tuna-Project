package net.tuna.mock.post.service;

import net.tuna.mock.post.dto.PostDto;

import java.util.List;

public interface PostService {
    List<PostDto> getPosts();
}
