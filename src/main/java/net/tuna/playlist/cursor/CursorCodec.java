package net.tuna.playlist.cursor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Component
public class CursorCodec {

    private static final String VERSION = "v1";

    public String encode(CursorDirection direction, CursorKey key) {
        String payload = String.join(
                "|",
                VERSION,
                direction.name(),
                key.getCreatedAt().toString(),
                String.valueOf(key.getPostId())
        );

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    public CursorRequest decode(String token) {
        if(token == null || token.isBlank()) {
            return CursorRequest.first();
        }

        try {
            String payload = new String(
                    Base64.getDecoder().decode(token),
                    StandardCharsets.UTF_8
            );

            String[] parts = payload.split("\\|", 4);

            if(parts.length != 4) {
                throw new IllegalArgumentException("Invalid token");
            }

            if(!VERSION.equals(parts[0])) {
                throw new IllegalArgumentException("Invalid token");
            }

            CursorDirection direction = CursorDirection.valueOf(parts[1]);

            LocalDateTime createdAt = LocalDateTime.parse(parts[2]);

            long postId = Long.parseLong(parts[3]);

            if(postId <= 0) {
                throw new IllegalArgumentException("Invalid postId");
            }

            return new CursorRequest(direction, new CursorKey(createdAt, postId));
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 커서입니다.");
        }
    }
}
