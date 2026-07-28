package net.tuna.playlist.repository;

import net.tuna.playlist.Playlist;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcPlaylistRepository implements PlaylistRepository {

    private static final RowMapper<Playlist> PLAYLIST_ROW_MAPPER =
            (resultSet, rowNum) -> new Playlist(
                    resultSet.getLong("id"),
                    resultSet.getLong("member_id"),
                    resultSet.getString("name"),
                    resultSet.getTimestamp("created_at").toLocalDateTime(),
                    resultSet.getLong("post_count")
            );

    private final JdbcTemplate jdbcTemplate;

    public JdbcPlaylistRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Playlist playlist) {
        jdbcTemplate.update("""
            INSERT INTO playlists (name, member_id)
            VALUES (?, ?)
        """, playlist.getName(), playlist.getMemberId());
    }

    @Override
    public List<Playlist> findAll(long memberId) {
        List<Playlist> playlists = jdbcTemplate.query("""
            SELECT 
                pl.id,
                pl.member_id,
                pl.name,
                pl.created_at,
                COUNT(ppm.post_id) AS post_count
            FROM playlists pl
            LEFT JOIN post_playlist_mapping ppm
                ON ppm.playlist_id = pl.id
            WHERE pl.member_id = ?
            GROUP BY
                pl.id,
                pl.member_id,
                pl.name,
                pl.created_at
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
    public void deleteById(long id, long memberId) {

    }
}
