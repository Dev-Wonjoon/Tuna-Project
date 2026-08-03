package net.tuna.playlist.cursor;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CursorKey {
    private final LocalDateTime createdAt;
    private final long postId;
}
