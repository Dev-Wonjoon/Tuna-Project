package net.tuna.post.dto;

import lombok.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PostDetailResponse {
    private long id;
    private String title;
    private String content;
    private String musicUrl;
    private String name;
    private int viewCount;
    private Long memberId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    //PostDto -> PostDetailResponse 변환
    public static PostDetailResponse from(PostDto postDto){
        if(postDto == null) return null;

        return PostDetailResponse.builder()
                .id(postDto.getId())
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .musicUrl(postDto.getMusicUrl())
                .name(postDto.getName())
                .viewCount(postDto.getViewCount())
                .memberId(postDto.getMemberId())
                .createdAt(postDto.getCreatedAt())
                .updatedAt(postDto.getUpdatedAt())
                .build();
    }


    //게시글이 수정되었는지 확인하는 로직
    public boolean isPostUpdate(){
        if(this.createdAt == null || this.updatedAt == null) return false;
        if(this.createdAt.equals(this.updatedAt)) return false;
        return this.updatedAt.isAfter(this.createdAt);
    }

    //수정되었는지 확인후 수정 X
    public String getFormattedDateCreate(){
        if(this.createdAt == null) return "";

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(this.createdAt, now);
        long seconds = duration.getSeconds();

        if (seconds < 60) return "방금 전";
        if (seconds < 3600) return (seconds/60) + "분 전";
        if (seconds < 86400) return (seconds/3600) + "시간 전";

        return this.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    //수정되었는지 확인후 수정 O
    public String getFormattedDateUpdate(){
        if(this.updatedAt == null) return "";

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(this.updatedAt, now);
        long seconds = duration.getSeconds();

        if (seconds < 60) return "(수정됨)방금 전";
        if (seconds < 3600) return "(수정됨)"+ (seconds/60) + "분 전";
        if (seconds < 86400) return "(수정됨)"+ (seconds/3600) + "시간 전";

        return "(수정됨)" + this.updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
