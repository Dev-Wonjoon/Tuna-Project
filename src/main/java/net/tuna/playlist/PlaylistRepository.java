package net.tuna.playlist;

import net.tuna.post.dto.PostDto;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository {
    public List<Playlist> findAll();
    public List<PostDto> findAllByPlaylistId(int playlistId, long memberId);
    public Optional<Playlist> findById(long id);
    public void save(Playlist playlist);
    public void deleteById(long id);
}
