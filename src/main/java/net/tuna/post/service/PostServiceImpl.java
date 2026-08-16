package net.tuna.post.service;

import net.tuna.cursor.*;
import net.tuna.music.*;
import net.tuna.post.Post;
import net.tuna.post.dto.PostDto;
import net.tuna.post.repository.PostQueryRepository;
import net.tuna.post.repository.JpaPostRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class PostServiceImpl implements PostService {
    private static final int MAX_PAGE_SIZE = 50;

    private final PostQueryRepository postQueryRepository;
    private final JpaPostRepository jpaPostRepository;
    private final MusicSourceRepository musicSourceRepository;
    private final MusicUrlResolverRegistry musicUrlResolverRegistry;
    private final CursorCodec cursorCodec;

    public PostServiceImpl(
            @Qualifier("jdbcTemplatePostRepository") PostQueryRepository postQueryRepository,
            JpaPostRepository jpaPostRepository,
            MusicSourceRepository musicSourceRepository,
            MusicUrlResolverRegistry resolverRegistry,
            CursorCodec cursorCodec
    ) {
        this.postQueryRepository = postQueryRepository;
        this.jpaPostRepository = jpaPostRepository;
        this.musicSourceRepository = musicSourceRepository;
        this.musicUrlResolverRegistry = resolverRegistry;
        this.cursorCodec = cursorCodec;
    }

    @Override
    @Transactional
    public long writePost(PostDto request, long memberId) {
        List<ResolvedMusicSource> resolvedMusicSources =
                resolveAll(request.getMusicUrls());

        List<MusicSource> musicSources =
                findOrCreateMusicSources(resolvedMusicSources);

        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                memberId
        );

        post.replaceMusicSources(musicSources);

        return jpaPostRepository.save(post).getId();
    }

    @Override
    @Transactional
    public void editPost(long postId, PostDto request) {
        Post post = jpaPostRepository.findAggregateById(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "게시글을 찾을 수 없습니다.")
                );

        List<ResolvedMusicSource> resolvedMusicSources =
                resolveAll(request.getMusicUrls());

        List<MusicSource> musicSources =
                findOrCreateMusicSources(resolvedMusicSources);

        post.update(
                request.getTitle(),
                request.getContent()
        );

        post.replaceMusicSources(musicSources);
    }

    @Override
    public PostDto getPost(long id) {
        return  postQueryRepository.findById(id);
    }

    @Override
    public List<PostDto> getPostsByMemberId(long id) {
        return postQueryRepository.findPostsByMemberId(id);
    }

    @Override
    @Transactional
    public void deletePost(long postId) {
        Post post = jpaPostRepository
                .findAggregateById(postId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "게시글을 찾을 수 없습니다."
                        )
                );
        jpaPostRepository.delete(post);
    }

    @Override
    @Transactional
    public void addViewCount(long postId) {
        int affectedRows = jpaPostRepository.increaseViewCount(postId);

        if(affectedRows != 1) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "게시글을 찾을 수 없습니다."
            );
        }
    }

    @Override
    public List<PostDto> getSearchPosts(String type, String value) {
        if (type.equals("searchTitleContent")) {
            return postQueryRepository.findByKeywordFromTitleContent(value);
        }
        if (type.equals("searchTitle")) {
            return postQueryRepository.findByKeywordFromTitle(value);
        }
        if (type.equals("searchContent")) {
            return postQueryRepository.findByKeywordFromContent(value);
        }
        if (type.equals("searchAuthor")) {
            return postQueryRepository.findByKeywordFromAuthor(value);
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
                postQueryRepository.findSlice(
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

    private List<ResolvedMusicSource> resolveAll(
            List<String> rawUrls
    ) {
        if(rawUrls == null || rawUrls.isEmpty()) {
            return List.of();
        }

        List<ResolvedMusicSource> resolved = new ArrayList<>();
        Set<MusicIdentity> identities = new HashSet<>();

        for(String rawUrl : rawUrls) {
            if(rawUrl == null || rawUrl.isBlank()) {
                continue;
            }

            String normalizedInput = rawUrl.trim();

            ResolvedMusicSource source = musicUrlResolverRegistry
                    .resolve(normalizedInput)
                    .orElseThrow(() ->
                            new MusicUrlValidationException(
                                    "지원하지 않는 음악 URL입니다: " + normalizedInput
                            ));

            MusicIdentity identity = new MusicIdentity(
                    source.provider(),
                    source.resourceType(),
                    source.resourceId()
            );

            if(!identities.add(identity)) {
                throw new MusicUrlValidationException("같은 음악을 중복 등록할 수 없습니다.");
            }

            resolved.add(source);
        }

        return List.copyOf(resolved);
    }

    private List<MusicSource> findOrCreateMusicSources(
            List<ResolvedMusicSource> resolvedMusicSources
    ) {
        List<MusicSource> results = new ArrayList<>(resolvedMusicSources.size());

        for(ResolvedMusicSource resolved : resolvedMusicSources) {
            musicSourceRepository.insertIfAbsent(
                    resolved.provider(),
                    resolved.resourceType(),
                    resolved.resourceId(),
                    resolved.canonicalUrl()
            );

            MusicSource musicSource = musicSourceRepository
                    .findByProviderAndResourceTypeAndResourceId(
                            resolved.provider(),
                            resolved.resourceType(),
                            resolved.resourceId()
                    )
                    .orElseThrow(() ->
                        new IllegalStateException("음악 출처 저장 후 조회에 실패했습니다.")
                    );

            results.add(musicSource);
        }

        return results;
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

    private record MusicIdentity(
            String provider,
            String resourceType,
            String resourceId
    ) {}
}
