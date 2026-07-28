package net.tuna.playlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {

    private long id;

    @Setter
    private long memberId;

    @Setter
    @NotBlank(message = "이 필드는 필수입니다.")
    @Size(min = 1, max = 255, message = "최소 1자에서 최대 255자 사이입니다.")
    private String name;


    @Setter
    private LocalDateTime createdAt;


    private long postCount;

    public Playlist(
            long id,
            long memberId,
            String name,
            LocalDateTime createdAt
    ) {
        this(id, memberId, name, createdAt, 0L);
    }
}
