package net.tuna.playlist.repository;

import net.tuna.post.dto.PostDto;

import java.util.List;

public interface PlaylistPostRepository {
    List<PostDto> findAllByPlaylistId(
            long playlistId,
            long memberId
    );
}
