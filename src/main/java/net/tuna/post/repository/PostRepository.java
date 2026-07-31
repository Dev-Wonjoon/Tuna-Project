package net.tuna.post.repository;

import net.tuna.post.dto.PostDto;

import java.util.List;
import java.util.Map;

public interface PostRepository {
    List<PostDto> findAll();
    void createPost(PostDto post);
    void updatePost(PostDto post);
    PostDto findById(long id);
    List<Map<String, Object>> findCommentsById(long id);
    List<PostDto> findPostsByMemberId(long id);
    void deleteById(long id);
    void addViewCount(long id);
    List<PostDto> findByKeywordFromTitle(String keyword);
    List<PostDto> findByKeywordFromContent(String keyword);
    List<PostDto> findByKeywordFromTitleContent(String keyword);
    List<PostDto> findByKeywordFromAuthor(String keyword);
}
