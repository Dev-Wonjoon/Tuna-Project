package net.tuna.post.service;

import net.tuna.cursor.*;
import net.tuna.post.dto.PostDto;
import net.tuna.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PostServiceImpl implements PostService {
    private static final int MAX_PAGE_SIZE = 50;

    private final PostRepository postRepository;
    private final CursorCodec cursorCodec;

    public PostServiceImpl(@Qualifier("jdbcTemplatePostRepository")PostRepository postRepository, CursorCodec cursorCodec) {
        this.postRepository = postRepository;
        this.cursorCodec = cursorCodec;
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

    @Override
    public CursorSlice<PostDto> getPostSlice(String cursorToken, int size) {
        validatePageSize(size);

        CursorRequest request = cursorCodec.decode(cursorToken);

        boolean firstRequest = request.isFirst();

        CursorDirection direction = firstRequest
                ? CursorDirection.NEXT
                : request.getDirection();

        List<PostDto> candidates =
                postRepository.findSlice(
                        request.getKey(),
                        direction,
                        size + 1
                );

        boolean hasExtra = candidates.size() > size;

        int endIndex = Math.min(size, candidates.size());

        List<PostDto> content = new ArrayList<>(candidates.subList(0, endIndex));

        if(direction == CursorDirection.PREVIOUS) {
            Collections.reverse(content);
        }

        if(content.isEmpty()) {
            return CursorSlice.empty();
        }

        boolean hasPrevious;
        boolean hasNext;

        if(firstRequest) {
            hasPrevious = false;
            hasNext = hasExtra;
        } else if(direction == CursorDirection.NEXT) {
            hasPrevious = true;
            hasNext = hasExtra;
        } else {
            hasPrevious = hasExtra;
            hasNext = true;
        }

        PostDto firstPost = content.get(0);
        PostDto lastPost = content.get(content.size() - 1);

        String previousCursor = hasPrevious
                ? cursorCodec.encode(
                        CursorDirection.PREVIOUS,
                        toCursorKey(firstPost))
                : null;

        String nextCursor = hasNext
                ? cursorCodec.encode(
                    CursorDirection.NEXT,
                    toCursorKey(lastPost))
                : null;

        return new CursorSlice<>(content, previousCursor, nextCursor);
    }

    private CursorKey toCursorKey(PostDto post) {
        return new CursorKey(
                post.getCreatedAt(),
                post.getId()
        );
    }

    private void validatePageSize(int size) {
        if(size < 1 || size > MAX_PAGE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "페이지 크기는 1 이상" + MAX_PAGE_SIZE + " 이하여야 합니다."
            );
        }
    }
}
