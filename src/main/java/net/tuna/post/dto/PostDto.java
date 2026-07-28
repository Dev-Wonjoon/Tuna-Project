package net.tuna.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PostDto {
    //Long으로 변경
    private Long id;
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;
    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content;
    private String musicUrl;
    private String authorEmail;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //게시글 등록화면 아이디 받아오기
    private Long memberId;


}
