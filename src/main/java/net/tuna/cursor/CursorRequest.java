package net.tuna.cursor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CursorRequest {
    private final CursorDirection direction;
    private final CursorKey key;

    public static CursorRequest first() {
        return new CursorRequest(null, null);
    }

    public boolean isFirst() {
        return key == null;
    }
}
