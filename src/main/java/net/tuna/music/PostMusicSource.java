package net.tuna.music;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.tuna.post.Post;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@Table(
        name = "post_music_sources",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_post_music_source",
                        columnNames = {"post_id", "music_source_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_post_music_source_order",
                        columnList = "post_id, sort_order, id"
                ),
                @Index(
                        name = "idx_post_music_source_source",
                        columnList = "music_source_id"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostMusicSource {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "post_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_post_music_source_post")
    )
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "music_source_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_post_music_source_source")
    )
    private MusicSource musicSource;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private PostMusicSource(Post post, MusicSource musicSource, int sortOrder
    ) {
        this.post = Objects.requireNonNull(post, "게시글은 필수입니다.");

        this.musicSource = Objects.requireNonNull(
                musicSource,
                "음악 출처는 필수입니다."
        );

        validateSortOrder(sortOrder);
        this.sortOrder = sortOrder;

    }

    public static PostMusicSource create(Post post, MusicSource musicSource, int sortOrder) {
        return new PostMusicSource(post, musicSource, sortOrder);
    }

    public void changeSortOrder(int sortOrder) {
        validateSortOrder(sortOrder);
        this.sortOrder = sortOrder;
    }

    public boolean hasSameSource(MusicSource musicSource) {
        return this.musicSource.hasSameIdentity(musicSource);
    }

    private static void validateSortOrder(int sortOrder) {
        if(sortOrder < 0) {
            throw new IllegalArgumentException("음악 순서는 0 이상이어야 합니다.");
        }
    }
}
