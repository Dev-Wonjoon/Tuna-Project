package net.tuna.post;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.tuna.music.MusicSource;
import net.tuna.music.PostMusicSource;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /* JDBC Template 코드에서 전환 할 동안 유지. */
    @Deprecated
    @Column(name = "music_url", length = 2048)
    private String legacyMusicUrl;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("sortOrder ASC, id ASC")
    private List<PostMusicSource> musicSources = new ArrayList<>();

    public Post(String title, String content, Long memberId) {
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.viewCount = 0;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void replaceMusicSources(List<MusicSource> orderedSources) {
        Objects.requireNonNull(
                orderedSources,
                "음악 출처 목록은 필수입니다."
        );

        validateNoDuplicateSources(orderedSources);

        List<PostMusicSource> reorderedSources = new ArrayList<>(orderedSources.size());

        for(int i = 0; i<orderedSources.size(); ++i) {
            MusicSource source = Objects.requireNonNull(
                    orderedSources.get(i),
                    "음악 출처는 null일 수 없습니다."
            );

            int sortOrder = i;
            PostMusicSource mapping = musicSources.stream()
                    .filter(existing -> existing.hasSameSource(source))
                    .findFirst()
                    .orElseGet(() ->
                            PostMusicSource.create(
                                    this,
                                    source,
                                    sortOrder
                            )
                    );

            mapping.changeSortOrder(i);
            reorderedSources.add(mapping);
        }

        musicSources.clear();
        musicSources.addAll(reorderedSources);

        syncLegacyMusicUrls();
    }

    public void addMusicSource(MusicSource musicSource) {
        PostMusicSource mapping = PostMusicSource.create(
                this,
                musicSource,
                musicSources.size()
        );
        boolean duplicated = musicSources.stream()
                .anyMatch(_mapping -> _mapping.hasSameSource(musicSource));

        if(duplicated) {
            throw new IllegalStateException("이미 등록된 음악입니다.");
        }

        musicSources.add(mapping);

        if(musicSources.size() == 1) {
            legacyMusicUrl = musicSource.getCanonicalUrl();
        }
    }

    public void removeMusicSource(PostMusicSource mapping) {
        musicSources.remove(mapping);
        reorderMusicSources();

        legacyMusicUrl = musicSources.isEmpty()
                ? null
                : musicSources.getFirst()
                  .getMusicSource()
                  .getCanonicalUrl();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    private void validateNoDuplicateSources(
            List<MusicSource> sources
    ) {
        for(int left=0; left < sources.size(); left++) {
            for(int right=left+1; right < sources.size(); right++) {
                if(sources.get(left).hasSameIdentity(sources.get(right))) {
                    throw new IllegalArgumentException("같은 음악을 중복 등록할 수 없습니다.");
                }
            }
        }
    }

    private void syncLegacyMusicUrls() {
        legacyMusicUrl = musicSources.isEmpty()
                ? null
                : musicSources.getFirst()
                  .getMusicSource()
                  .getCanonicalUrl();
    }

    private void reorderMusicSources() {
        for(int index = 0; index < musicSources.size(); index++) {
            musicSources.get(index).changeSortOrder(index);
        }
    }
}
