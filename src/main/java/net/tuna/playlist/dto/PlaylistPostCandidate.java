package net.tuna.playlist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.tuna.cursor.CursorKey;
import net.tuna.post.dto.PostDto;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PlaylistPostCandidate {

    private final PostDto post;

    // post_playlist_mapping.createdAt
    // 즉 플레이리스트에 추가된 날짜
    private final LocalDateTime addedAt;

    public CursorKey toCursorKey() {
        return new CursorKey(
                addedAt,
                post.getId()
        );
    }

}
