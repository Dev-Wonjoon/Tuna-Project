package net.tuna.playlist.repository;

import net.tuna.cursor.CursorDirection;
import net.tuna.cursor.CursorKey;
import net.tuna.playlist.dto.PlaylistMusicCandidate;
import net.tuna.playlist.dto.PlaylistPostCandidate;
import net.tuna.post.dto.PostDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPlaylistPostRepository implements PlaylistPostRepository {

    private static final RowMapper<PostDto> POST_ROW_MAPPER =
            (resultSet, rowNum) -> {
                PostDto post = new PostDto();

                post.setId(resultSet.getLong("id"));
                post.setMemberId(resultSet.getLong("member_id"));
                post.setTitle(resultSet.getString("title"));
                post.setContent(resultSet.getString("content"));
                post.setMusicUrl(resultSet.getString("music_url"));
                post.setAuthorEmail(resultSet.getString("author_email"));
                post.setCommentCount(resultSet.getInt("comment_count"));
                post.setViewCount(resultSet.getInt("view_count"));
                post.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                post.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());

                return post;
            };

    private static final RowMapper<PlaylistPostCandidate> POST_CANDIDATE_ROW_MAPPER =
            (resultSet, rowNum) -> {
                PostDto post = POST_ROW_MAPPER.mapRow(resultSet, rowNum);
                LocalDateTime addedAt = resultSet.getObject("added_at", LocalDateTime.class);

                return new PlaylistPostCandidate(post, addedAt);
            };

    private static final RowMapper<PlaylistMusicCandidate> MUSIC_CANDIDATE_ROW_MAPPER =
            (resultSet, rowNum) -> new PlaylistMusicCandidate(
                    resultSet.getLong("post_id"),
                    resultSet.getString("title"),
                    resultSet.getString("music_url"),
                    resultSet.getObject("added_at", LocalDateTime.class)
            );

    private final JdbcTemplate jdbcTemplate;

    public JdbcPlaylistPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<PostDto> findAllByPlaylistId(long playlistId, long memberId) {
        return jdbcTemplate.query("""
            SELECT
                p.id,
                p.member_id,
                p.title,
                p.content,
                p.music_url,
                p.view_count,
                p.created_at,
                p.updated_at,
                m.name,
                m.email AS author_email,
                COALESCE(cc.comment_count, 0) AS comment_count
            FROM post_playlist_mapping ppm
            JOIN playlists pl
                ON pl.id = ppm.playlist_id
            JOIN posts p
                ON p.id = ppm.post_id
            JOIN members m
                ON m.id = p.member_id
            LEFT JOIN (
                SELECT post_id, COUNT(*) AS comment_count
                FROM comments
                GROUP BY post_id
            ) cc
                ON cc.post_id = p.id
            WHERE ppm.playlist_id = ?
                AND pl.member_id = ?
            ORDER BY
                ppm.created_at DESC,
                ppm.post_id DESC
        """, POST_ROW_MAPPER, playlistId, memberId);
    }

    @Override
    public int add(long playlistId, long postId, long memberId) {
        return jdbcTemplate.update("""
            INSERT INTO post_playlist_mapping (
                post_id,
                playlist_id
            )
            SELECT
                p.id,
                pl.id
            FROM posts p
            JOIN playlists pl
                ON pl.id = ?
            WHERE p.id = ?
                AND pl.member_id = ?
                AND NOT EXISTS (
                    SELECT 1
                    FROM post_playlist_mapping ppm
                    WHERE ppm.post_id = p.id
                        AND ppm.playlist_id = pl.id
                )
        """, playlistId, postId, memberId);
    }

    @Override
    public int remove(long playlistId, long postId, long memberId) {
        return jdbcTemplate.update("""
            DELETE ppm
            FROM post_playlist_mapping ppm
            JOIN playlists pl
                ON pl.id = ppm.playlist_id
            WHERE ppm.post_id = ?
                AND ppm.playlist_id = ?
                AND pl.member_id = ?
        """, postId, playlistId, memberId);
    }

    @Override
    public int removeAll(long playlistId, long memberId) {
        return jdbcTemplate.update("""
            DELETE ppm
            FROM post_playlist_mapping ppm
            JOIN playlists pl
                ON pl.id = ppm.playlist_id
            WHERE ppm.playlist_id = ?
                AND pl.member_id = ?
        """, playlistId, memberId);
    }

    @Override
    public int removeByPostIds(long playlistId, List<Long> postIds, long memberId) {
        if(postIds == null || postIds.isEmpty()) {
            return 0;
        }

        List<Long> distinctPostIds = postIds.stream()
                .distinct()
                .toList();

        String placeholders = String.join(
                ", ",
                Collections.nCopies(distinctPostIds.size(), "?")
        );

        String sql = """
            DELETE ppm
            FROM post_playlist_mapping ppm
            JOIN playlists pl
                ON pl.id = ppm.playlist_id
            WHERE ppm.playlist_id = ?
                AND pl.member_id = ?
                AND ppm.post_id IN (%s)
        """.formatted(placeholders);

        List<Object> parameters = new ArrayList<>();

        parameters.add(playlistId);
        parameters.add(memberId);
        parameters.addAll(distinctPostIds);

        return jdbcTemplate.update(sql, parameters.toArray());
    }

    @Override
    public List<PlaylistMusicCandidate> findYoutubeCandidate(long playlistId, long memberId, CursorKey cursor, CursorDirection direction, int limit) {
        boolean previous = direction == CursorDirection.PREVIOUS;

        String cursorCondition = "";

        List<Object> parameters = new ArrayList<>();

        parameters.add(playlistId);
        parameters.add(memberId);

        if(cursor != null) {
            String comparison = previous ? ">" : "<";

            cursorCondition = """
                AND (
                    ppm.created_at %s ?
                    OR (
                        ppm.created_at = ?
                        AND ppm.post_id %s ?
                    )
                )
            """.formatted(
                    comparison,
                    comparison
            );

            parameters.add(cursor.getCreatedAt());
            parameters.add(cursor.getCreatedAt());
            parameters.add(cursor.getId());
        }

        String order = previous ? "ASC" : "DESC";

        String sql = """
            SELECT
                p.id AS post_id,
                p.title,
                p.music_url,
                ppm.created_at AS added_at
            FROM post_playlist_mapping ppm
            JOIN playlists pl
                ON pl.id = ppm.playlist_id
            JOIN posts p
                ON p.id = ppm.post_id
            WHERE ppm.playlist_id = ?
                AND pl.member_id = ?
                AND TRIM(p.music_url) <> ''
                AND (
                    LOWER(TRIM(p.music_url))
                        LIKE 'http%%://youtube.com/%%'
                    OR LOWER(TRIM(p.music_url))
                        LIKE 'http%%://youtube.com/%%'
                    OR LOWER(TRIM(p.music_url))
                        LIKE 'http%%://m.youtube.com/%%'
                    OR LOWER(TRIM(p.music_url))
                        LIKE 'http%%://music.youtube.com/%%'
                    OR LOWER(TRIM(p.music_url))
                        LIKE 'http%%://youtu.be/%%'
                    OR LOWER(TRIM(p.music_url))
                        LIKE 'http%%://youtube-nocookie.com/%%'
                    OR LOWER(TRIM(p.music_url))
                        LIKE 'http%%://www.youtube-nocookie.com/%%'
                    OR LOWER(TRIM(p.music_url))
                        LIKE 'http%%://www.youtube.com/%%'
                )
                %s
            ORDER BY
                ppm.created_at %s,
                ppm.post_id %s
            LIMIT ?
        """.formatted(
                cursorCondition,
                order,
                order
        );

        parameters.add(limit);

        return jdbcTemplate.query(
                sql,
                MUSIC_CANDIDATE_ROW_MAPPER,
                parameters.toArray()
        );
    }

    @Override
    public List<PlaylistPostCandidate> findPostCandidates(long playlistId, long memberId, CursorKey cursor, CursorDirection direction, int limit) {
        if(limit <= 0) {
            throw new IllegalArgumentException("limit은 1 이상이어야 합니다.");
        }

        boolean previous = direction == CursorDirection.PREVIOUS;

        String cursorCondition = "";

        List<Object> parameters = new ArrayList<>();

        parameters.add(playlistId);
        parameters.add(memberId);

        if(cursor != null) {
            String comparison = previous ? ">" : "<";

            cursorCondition = """
                AND (
                    ppm.created_at < ?
                    OR (
                        ppm.created_at = ?
                        AND ppm.post_id < ?
                    )
                )
            """.formatted(comparison, comparison);

            parameters.add(cursor.getCreatedAt());
            parameters.add(cursor.getCreatedAt());
            parameters.add(cursor.getId());
        }

        String order = previous ? "ASC" : "DESC";

        String sql = """
            SELECT
                p.id,
                p.member_id,
                p.title,
                p.content,
                p.music_url,
                p.view_count,
                p.created_at,
                p.updated_at,
                m.name,
                m.email AS author_email,
                COALESCE(c.comment_count, 0) AS comment_count,
                ppm.created_at AS added_at
            FROM post_playlist_mapping ppm
            JOIN playlists pl
                ON pl.id = ppm.playlist_id
            JOIN posts p
                ON p.id = ppm.post_id
            JOIN members m
                ON m.id = p.member_id
            LEFT JOIN (
                SELECT post_id, COUNT(*) AS comment_count
                FROM comments
                GROUP BY post_id
            ) c
                ON c.post_id = p.id
            WHERE ppm.playlist_id = ?
                AND pl.member_id = ?
                %s
            ORDER BY
                ppm.created_at %s,
                ppm.post_id %s
            LIMIT ?
        """.formatted(cursorCondition, order, order);

        parameters.add(limit);

        return jdbcTemplate.query(
                sql,
                POST_CANDIDATE_ROW_MAPPER,
                parameters.toArray()
        );
    }

    @Override
    public Optional<String> findMusicUrlByPostId(long postId) {
        return jdbcTemplate.query(
                "SELECT music_url FROM posts WHERE id = ?",
                resultSet -> {
                    if(!resultSet.next()) {
                        return Optional.empty();
                    }

                    return Optional.ofNullable(resultSet.getString("music_url"));
                },
                postId
        );
    }
}
