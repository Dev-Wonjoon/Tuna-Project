package net.tuna.music;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MusicSourceRepository extends JpaRepository<MusicSource, Long> {
    Optional<MusicSource> findByProviderAndResourceTypeAndResourceId(
            String provider,
            String resourceType,
            String resourceId
    );

    @Modifying(flushAutomatically = true)
    @Query(value = """
        INSERT INTO music_sources (
            provider,
            resource_type,
            resource_id,
            canonical_url
        )
        VALUES (
            :provider,
            :resourceType,
            :resourceId,
            :canonicalUrl
        )
        ON DUPLICATE KEY UPDATE id = id
    """, nativeQuery = true)
    int insertIfAbsent(
            @Param("provider") String provider,
            @Param("resourceType") String resourceType,
            @Param("resourceId") String resourceId,
            @Param("canonicalUrl") String canonicalUrl
    );
}
