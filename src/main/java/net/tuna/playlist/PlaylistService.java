package net.tuna.playlist;

import net.tuna.playlist.repository.PlaylistPostRepository;
import net.tuna.playlist.repository.PlaylistRepository;
import net.tuna.post.dto.PostDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistPostRepository playlistPostRepository;

    public PlaylistService(
            PlaylistRepository playlistRepository,
            PlaylistPostRepository playlistPostRepository
    ) {
        this.playlistRepository = playlistRepository;
        this.playlistPostRepository = playlistPostRepository;
    }

    public void createPlaylist(Playlist playlist) {
        playlistRepository.save(playlist);
    }

    public Playlist getPlaylistById(long playlistId, long memberId) {
        return playlistRepository.findById(
                playlistId,
                memberId
        );
    }

    public List<PostDto> getPosts(
            long playlistId,
            long memberId
    ) {
        return playlistPostRepository.findAllByPlaylistId(
                playlistId,
                memberId
        );
    }

    public List<Playlist> getPlaylists(long memberId) {
        return playlistRepository.findAll(memberId);
    }
}
