package net.tuna.playlist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.tuna.playlist.cursor.CursorKey;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PlaylistMusicCandidate {

    private final long postId;
    private final String title;
    private final String musicUrl;
    private final LocalDateTime addedAt;

    public CursorKey toCursorKey() {
        return new CursorKey(addedAt, postId);
    }
}
