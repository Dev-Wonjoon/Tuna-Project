package net.tuna.playlist.repository;

import net.tuna.playlist.dto.Playlist;

import java.util.List;

public interface PlaylistRepository {
    public List<Playlist> findAll(long memberId);
    public Playlist findById(long id, long memberId);
    public long save(Playlist playlist);
    public int deleteById(long playlistId, long memberId);
    int updateName(long id, long memberId, String name);
}
