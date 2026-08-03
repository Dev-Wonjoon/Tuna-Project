package net.tuna.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CommentDto {
    private Long id;
    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content;
    private String name;
    private Long memberId;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
