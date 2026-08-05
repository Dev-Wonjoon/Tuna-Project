package net.tuna.post.repository;

import net.tuna.cursor.CursorDirection;
import net.tuna.cursor.CursorKey;
import net.tuna.post.dto.PostDto;

import java.util.List;
import java.util.Map;

public interface PostRepository {
    long createPost(PostDto post);
    void updatePost(PostDto post);
    PostDto findById(long id);
    List<PostDto> findPostsByMemberId(long id);
    void deleteById(long id);
    void addViewCount(long id);
    List<PostDto> findByKeywordFromTitle(String keyword);
    List<PostDto> findByKeywordFromContent(String keyword);
    List<PostDto> findByKeywordFromTitleContent(String keyword);
    List<PostDto> findByKeywordFromAuthor(String keyword);
    List<PostDto> findSlice(CursorKey cursor, CursorDirection direction, int limit);
    long findAllByCommentCount(long postId);
}
