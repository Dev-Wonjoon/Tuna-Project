package net.tuna.mock.post.repository;

import net.tuna.mock.post.dto.PostDto;

import java.util.List;

public interface PostRepository {
    List<PostDto> findAll();
}
