package net.tuna.post.service;

import net.tuna.post.dto.PostDto;

import java.util.List;
import java.util.Map;

public interface PostService {
    List<PostDto> getPosts();
    long writePost(PostDto post);
    void editPost(PostDto post);
    PostDto getPost(long id);
    List<Map<String,Object>> getComments(long id);
    List<PostDto> getPostsByMemberId(long id);
    void deletePost(long id);
    void addViewCount(long id);
    List<PostDto> getSearchPosts(String type, String keyword);
}
