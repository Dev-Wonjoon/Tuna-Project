package net.tuna.mock.post.dto;

import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PostDto {
    private int id;
    private String title;
    private String content;
    private String musicUrl;
    private String authorEmail;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
