package net.tuna.playlist.repository;

import net.tuna.post.dto.PostDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class JdbcPlaylistPostRepository implements PlaylistPostRepository {

    private static final RowMapper<PostDto> POST_ROW_MAPPER =
            (resultSet, rowNum) -> {
                PostDto post = new PostDto();

                post.setId(resultSet.getLong("id"));
                post.setTitle(resultSet.getString("title"));
                post.setContent(resultSet.getString("content"));
                post.setMusicUrl(resultSet.getString("musicUrl"));
                post.setAuthorEmail(resultSet.getString("authorEmail"));
                post.setViewCount(resultSet.getInt("viewCount"));
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
                p.title,
                p.content,
                p.music_url,
                m.email AS author_email,
                p.view_count,
                p.created_at,
                p.updated_at
            FROM post_playlist_mapping ppm
            JOIN playlists pl
                ON pl.id = ppm.playlist_id
            JOIN posts p
                ON p.id = ppm.post_id
            WHERE pl.id = ?
                AND pl.member_id = ?
            ORDER BY ppm.created_at DESC
        """, POST_ROW_MAPPER, playlistId, memberId);
    }
}
