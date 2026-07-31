package net.tuna.playlist.repository;

import net.tuna.post.dto.PostDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
                post.setViewCount(resultSet.getInt("view_count"));
                post.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                post.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());

                return post;
            };

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
                m.email AS author_email
            FROM post_playlist_mapping ppm
            JOIN playlists pl
                ON pl.id = ppm.playlist_id
            JOIN posts p
                ON p.id = ppm.post_id
            JOIN members m
                ON m.id = p.member_id
            WHERE ppm.playlist_id = ?
                AND pl.member_id = ?
                AND ppm.member_id = pl.member_id
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
                playlist_id,
                member_id
            )
            SELECT
                p.id,
                pl.id,
                pl.member_id
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
                        AND ppm.member_id = pl.member_id
            )
        """,
                playlistId,
                postId,
                memberId
        );

    }

    @Override
    public int remove(long playlistId, long postId, long memberId) {
        return jdbcTemplate.update("""
            DELETE FROM post_playlist_mapping ppm
            WHERE post_id = ?
                AND playlist_id = ?
                AND member_id = ?
                AND playlist_id IN (
                    SELECT id
                    FROM playlists
                    WHERE id = ?
                        AND member_id = ?
                )
        """,
                postId,
                playlistId,
                memberId,
                playlistId,
                memberId
        );
    }

    @Override
    public int removeAll(long playlistId, long memberId) {
        return jdbcTemplate.update("""
                    DELETE FROM post_playlist_mapping ppm
                    WHERE playlist_id = ?
                        AND member_id = ?
                        AND playlist_id IN (
                            SELECT id
                            FROM playlists
                            WHERE id = ?
                                AND member_id = ?
                        )
                    """,
                playlistId,
                memberId,
                playlistId,
                memberId
        );
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
            DELETE FROM post_playlist_mapping ppm
            WHERE playlist_id = ?
                AND member_id = ?
                AND post_id IN (%s)
                AND playlist_id IN (
                    SELECT id
                    FROM playlists
                    WHERE id = ?
                        AND member_id = ?
                )
        """.formatted(placeholders);

        List<Object> paramaters = new ArrayList<>();

        paramaters.add(playlistId);
        paramaters.add(memberId);
        paramaters.addAll(distinctPostIds);
        paramaters.add(playlistId);
        paramaters.add(memberId);

        return jdbcTemplate.update(sql, paramaters.toArray());
    }
}
