package net.tuna.playlist.repository;

import net.tuna.playlist.Playlist;

import java.util.List;

public interface PlaylistRepository {
    public List<Playlist> findAll(long memberId);
    public Playlist findById(long id, long memberId);
    public long save(Playlist playlist);
    public void deleteById(long id, long memberId);
}
