package net.tuna.playlist.repository;

import net.tuna.playlist.cursor.CursorDirection;
import net.tuna.playlist.cursor.CursorKey;
import net.tuna.playlist.dto.PlaylistMusicCandidate;
import net.tuna.post.dto.PostDto;

import java.util.List;

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
}
