package net.tuna.playlist.repository;

import net.tuna.playlist.dto.Playlist;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class    JdbcPlaylistRepository implements PlaylistRepository {

    private static final RowMapper<Playlist> PLAYLIST_ROW_MAPPER =
            (resultSet, rowNum) -> new Playlist(
                    resultSet.getLong("id"),
                    resultSet.getLong("member_id"),
                    resultSet.getString("name"),
                    resultSet.getTimestamp("created_at").toLocalDateTime(),
                    resultSet.getLong("post_count"),
                    resultSet.getString("image_url")
            );

    private final JdbcTemplate jdbcTemplate;

    public JdbcPlaylistRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long save(Playlist playlist) {
        String sql = """
        INSERT INTO playlists (name, image_url, member_id)
        VALUES (?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            stmt.setString(1, playlist.getName());
            stmt.setString(2, playlist.getImageUrl());
            stmt.setLong(3, playlist.getMemberId());

            return stmt;
        }, keyHolder);

        Number key = keyHolder.getKey();

        if(key == null) {
            throw new IllegalStateException("생성된 플레이리스트 ID를 가져올 수 없습니다.");
        }

        return key.longValue();
    }

    @Override
    public int deleteById(long playlistId, long memberId) {
        return jdbcTemplate.update("""
            DELETE FROM playlists
            WHERE id = ?
                AND member_id = ?
        """, playlistId, memberId);
    }

    @Override
    public List<Playlist> findAll(long memberId) {
        List<Playlist> playlists = jdbcTemplate.query("""
            SELECT
                pl.id,
                pl.member_id,
                pl.name,
                pl.created_at,
                pl.image_url,
                COUNT(ppm.post_id) AS post_count
            FROM playlists pl
            LEFT JOIN post_playlist_mapping ppm
                ON ppm.playlist_id = pl.id
            WHERE pl.member_id = ?
            GROUP BY
                pl.id,
                pl.member_id,
                pl.name,
                pl.created_at,
                pl.image_url
            ORDER BY pl.created_at DESC
        """, PLAYLIST_ROW_MAPPER, memberId);

        return playlists;
    }

    @Override
    public Playlist findById(long playlistId, long memberId) {
        Playlist playlist = jdbcTemplate.queryForObject("""
            SELECT
                pl.id,
                pl.member_id,
                pl.name,
                pl.created_at,
                pl.image_url,
                COUNT(ppm.post_id) AS post_count
            FROM playlists pl
            LEFT JOIN post_playlist_mapping ppm
                ON ppm.playlist_id = pl.id
            WHERE pl.id = ?
                AND pl.member_id = ?
            GROUP BY
                pl.id,
                pl.member_id,
                pl.name,
                pl.created_at
            """, PLAYLIST_ROW_MAPPER, playlistId, memberId);
        return playlist;
    }

    @Override
    public int updateName(long id, long memberId, String name) {
        return jdbcTemplate.update("""
            UPDATE playlists
            SET name = ?
            WHERE id = ?
                AND member_id = ?
        
        """,
                name,
                id,
                memberId
        );
    }

    @Override
    public int countByMemberId(long memberId) {
        Integer count = jdbcTemplate.queryForObject("""
            SELECT COUNT(*)
            FROM playlists
            WHERE member_id = ?
        """, Integer.class, memberId);

        return count == null ? 0 : count;
    }

    @Override
    public void lockMember(long memberId) {
        jdbcTemplate.queryForObject("""
            SELECT id
            FROM members
            WHERE id = ?
            FOR UPDATE
        """, Long.class, memberId);
    }
}
