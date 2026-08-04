package net.tuna.playlist.repository;

import net.tuna.cursor.CursorDirection;
import net.tuna.cursor.CursorKey;
import net.tuna.playlist.dto.PlaylistMusicCandidate;
import net.tuna.playlist.dto.PlaylistPostCandidate;
import net.tuna.post.dto.PostDto;

import java.util.List;
import java.util.Optional;

public interface PlaylistPostRepository {
    List<PostDto> findAllByPlaylistId(
            long playlistId,
            long memberId
    );

    int add(long playlistId, long postId, long memberId);

    int remove(long playlistId, long postId, long memberId);

    int removeAll(long playlistId, long memberId);

    int removeByPostIds(long playlistId, List<Long> postIds, long memberId);

    List<PlaylistMusicCandidate> findYoutubeCandidate(
            long playlistId,
            long memberId,
            CursorKey cursor,
            CursorDirection direction,
            int limit
    );

    List<PlaylistPostCandidate> findPostCandidates(
            long playlistId,
            long memberId,
            CursorKey cursor,
            CursorDirection direction,
            int limit
    );

    Optional<String> findMusicUrlByPostId(long postId);
}
