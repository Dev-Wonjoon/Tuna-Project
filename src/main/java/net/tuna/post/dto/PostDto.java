package net.tuna.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PostDto {

    private Long id;
    private String name;

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content;

    @Builder.Default
    private List<String> musicUrls = new ArrayList<>();

    private String thumbnailUrl;
    private String authorEmail;
    private int viewCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long memberId;

    @Deprecated
    public String getMusicUrl() {
        return musicUrls == null || musicUrls.isEmpty()
                ? null
                : musicUrls.getFirst();
    }

    @Deprecated
    public void setMusicUrl(String musicUrl) {
        if(musicUrl == null || musicUrl.isBlank()) {
            this.musicUrls = new ArrayList<>();
            return;
        }

        this.musicUrls = new ArrayList<>(List.of(musicUrl));
    }

}
