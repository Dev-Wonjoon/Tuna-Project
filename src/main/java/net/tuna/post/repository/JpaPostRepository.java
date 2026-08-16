package net.tuna.post.repository;

import net.tuna.post.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaPostRepository extends JpaRepository<Post, Long> {
    @EntityGraph(attributePaths = "musicSources.musicSource")
    @Query("""
        SELECT DISTINCT p
        FROM Post p
        WHERE p.id = :id
    """)
    Optional<Post> findAggregateById(@Param("id") long id);

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("""
        UPDATE Post p
        SET p.viewCount = p.viewCount + 1
        WHERE p.id = :id
    """)
    int increaseViewCount(@Param("id") long id);
}
