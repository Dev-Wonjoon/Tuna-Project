package net.tuna.playlist.repository;

import net.tuna.playlist.dto.Playlist;

import java.util.List;

public interface PlaylistRepository {
    List<Playlist> findAll(long memberId);
    Playlist findById(long id, long memberId);
    long save(Playlist playlist);
    int deleteById(long playlistId, long memberId);
    int updateName(long id, long memberId, String name);
    int countByMemberId(long memberId);
    void lockMember(long memberId);
}
