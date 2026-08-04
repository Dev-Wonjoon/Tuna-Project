package net.tuna.playlist.service;

import net.tuna.cursor.CursorCodec;
import net.tuna.cursor.CursorDirection;
import net.tuna.cursor.CursorKey;
import net.tuna.cursor.CursorRequest;
import net.tuna.cursor.CursorSlice;
import net.tuna.playlist.PlaylistMusicUrlChecker;
import net.tuna.playlist.dto.PlaylistPostCandidate;
import net.tuna.playlist.repository.PlaylistPostRepository;
import net.tuna.post.dto.PostDto;
import net.tuna.utils.MusicThumbnailResolver;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PlaylistPostService {

    private static final int PAGE_SIZE = 2;

    private final PlaylistMusicUrlChecker playlistMusicUrlChecker;
    private final PlaylistPostRepository playlistPostRepository;
    private final MusicThumbnailResolver musicThumbnailResolver;
    private final CursorCodec cursorCodec;

    public PlaylistPostService(
            PlaylistMusicUrlChecker playlistMusicUrlChecker, PlaylistPostRepository playlistPostRepository,
            CursorCodec cursorCodec,
            MusicThumbnailResolver musicThumbnailResolver
    ) {
        this.playlistMusicUrlChecker = playlistMusicUrlChecker;
        this.playlistPostRepository = playlistPostRepository;
        this.musicThumbnailResolver = musicThumbnailResolver;
        this.cursorCodec = cursorCodec;
    }

    public List<PostDto> getPosts(long playlistId, long memberId) {
        return playlistPostRepository.findAllByPlaylistId(
                playlistId,
                memberId
        ).stream().map(this::attachThumbnail).toList();
    }

    public boolean addPost(long playlistId, long postId, long memberId) {
        playlistMusicUrlChecker.validate(postId);
        try {
            int affectedRows = playlistPostRepository.add(playlistId, postId, memberId);

            return affectedRows == 1;
        } catch (DuplicateKeyException exception) {
            return false;
        }
    }

    public boolean removePost(long playlistId, long postId, long memberId) {
        int affectedRows = playlistPostRepository.remove(playlistId, postId, memberId);

        return affectedRows == 1;
    }

    @Transactional
    public int removePosts(
            long playlistId,
            List<Long> postIds,
            long memberId
    ) {
        return playlistPostRepository.removeByPostIds(
                playlistId,
                postIds,
                memberId
        );
    }

    @Transactional
    public int removeAllPosts(long playlistId, long memberId) {
        return playlistPostRepository.removeAll(playlistId, memberId);
    }

    public CursorSlice<PostDto> getPostSlice(
            long playlistId,
            long memberId,
            String cursorToken
    ) {
        CursorRequest request = cursorCodec.decode(cursorToken);

        boolean firstRequest = request.isFirst();

        CursorDirection direction = firstRequest
                ? CursorDirection.NEXT
                : request.getDirection();

        CursorKey cursorKey = request.getKey();

        List<PlaylistPostCandidate> candidates =
                playlistPostRepository.findPostCandidates(
                        playlistId,
                        memberId,
                        cursorKey,
                        direction,
                        PAGE_SIZE + 1
                );

        boolean hasExtra = candidates.size() > PAGE_SIZE;

        int endIndex = Math.min(PAGE_SIZE, candidates.size());

        List<PlaylistPostCandidate> visibleItems = new ArrayList<>(candidates.subList(0, endIndex));

        if(direction == CursorDirection.PREVIOUS) {
            Collections.reverse(visibleItems);
        }

        if(visibleItems.isEmpty()) {
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

        PlaylistPostCandidate firstItem = visibleItems.get(0);

        PlaylistPostCandidate lastItem = visibleItems.get(visibleItems.size() - 1);

        String previousCursor = hasPrevious
                ? cursorCodec.encode(CursorDirection.PREVIOUS, firstItem.toCursorKey())
                : null;

        String nextCursor = hasNext
                ? cursorCodec.encode(CursorDirection.NEXT, lastItem.toCursorKey())
                : null;

        List<PostDto> content = visibleItems.stream()
                .map(PlaylistPostCandidate::getPost)
                .map(this::attachThumbnail)
                .toList();

        return new CursorSlice<>(content, previousCursor, nextCursor);
    }

    private PostDto attachThumbnail(PostDto post) {
        post.setThumbnailUrl(
                musicThumbnailResolver.resolve(post.getMusicUrl())
        );

        return post;
    }
}
