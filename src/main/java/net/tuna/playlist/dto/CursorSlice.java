package net.tuna.playlist.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CursorSlice<T> {

    private final List<T> content;
    private final String previousCursor;
    private final String nextCursor;

    public CursorSlice(
            List<T> content,
            String previousCursor,
            String nextCursor
    ) {
        this.content = content == null
                ? List.of()
                : List.copyOf(content);

        this.previousCursor = previousCursor;
        this.nextCursor = nextCursor;
    }

    public static <T> CursorSlice<T> empty() {
        return new CursorSlice<>(
                List.of(),
                null,
                null
        );
    }

    public boolean hasPrevious() {
        return previousCursor != null;
    }

    public boolean hasNext() {
        return nextCursor != null;
    }
}
