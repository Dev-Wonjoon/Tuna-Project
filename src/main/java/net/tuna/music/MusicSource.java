package net.tuna.music;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@Table(
        name = "music_sources",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_music_source_identity",
                columnNames = {"provider", "resource_type", "resource_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MusicSource {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String provider;

    @Column(name="resource_type", nullable = false, length = 32)
    private String resourceType;

    @Column(name = "resource_id", nullable = false, length = 255)
    private String resourceId;

    @Column(name = "canonical_url", nullable = false, length = 2048)
    private String canonicalUrl;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    private MusicSource(
            String provider,
            String resourceType,
            String resourceId,
            String canonicalUrl
    ) {
        this.provider = Objects.requireNonNull(provider);
        this.resourceType = Objects.requireNonNull(resourceType);
        this.resourceId = Objects.requireNonNull(resourceId);
        this.canonicalUrl = Objects.requireNonNull(canonicalUrl);
    }

    public static MusicSource from(ResolvedMusicSource resolved) {
        return new MusicSource(
                resolved.provider(),
                resolved.resourceType(),
                resolved.resourceId(),
                resolved.canonicalUrl()
        );
    }

    public boolean hasSameIdentity(MusicSource other) {
        return provider.equals(other.provider)
                && resourceType.equals(other.resourceType)
                && resourceId.equals(other.resourceId);
    }
}
