package net.tuna.post.service;

import net.tuna.cursor.CursorSlice;
import net.tuna.post.dto.PostDto;

import java.util.List;
import java.util.Map;

public interface PostService {
    long writePost(PostDto post, long memberId);
    void editPost(long postId, PostDto post);
    PostDto getPost(long id);
    List<PostDto> getPostsByMemberId(long id);
    void deletePost(long id);
    void addViewCount(long id);
    List<PostDto> getSearchPosts(String type, String keyword);
    CursorSlice<PostDto> getPostSlice(String cursorToken, int size);
}
