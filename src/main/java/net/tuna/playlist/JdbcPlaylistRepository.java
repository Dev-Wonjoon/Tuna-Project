package net.tuna.playlist;

import net.tuna.post.dto.PostDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPlaylistRepository implements PlaylistRepository {

    private static final RowMapper<Playlist> PLAYLIST_ROW_MAPPER =
            (resultSet, rowNum) -> new Playlist(
                    resultSet.getLong("id"),
                    resultSet.getLong("member_id"),
                    resultSet.getString("name"),
                    resultSet.getTimestamp("created_at").toLocalDateTime()
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
    public List<Playlist> findAll() {
        return List.of();
    }

    @Override
    public List<PostDto> findAllByPlaylistId(int playlistId, long memberId) {
        return List.of();
    }

    @Override
    public Optional<Playlist> findById(long id) {
        return Optional.empty();
    }

    @Override
    public void deleteById(long id) {

    }
}
