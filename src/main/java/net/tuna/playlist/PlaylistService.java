package net.tuna.playlist;

import net.tuna.playlist.repository.PlaylistPostRepository;
import net.tuna.playlist.repository.PlaylistRepository;
import net.tuna.post.dto.PostDto;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    public long createPlaylist(
            Playlist playlist
    ) {
        playlist.setName(playlist.getName().trim());
        return playlistRepository.save(playlist);
    }

    @Transactional
    public long createPlaylistWithPost(
            Playlist playlist,
            long postId
    ) {
        long playlistId = createPlaylist(playlist);

        int affectedRows = playlistPostRepository.add(
                playlistId,
                postId,
                playlist.getMemberId()
        );

        if(affectedRows != 1) {
            throw new IllegalStateException(
                    "추가할 게시글을 찾을 수 없습니다."
            );
        }

        return playlistId;
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

    public void updatePlaylistName(
            long playlistId,
            long memberId,
            String name
    ) {
        int affectedRows = playlistRepository.updateName(
                playlistId,
                memberId,
                name.trim()
        );

        if(affectedRows != 1) {
            throw new IllegalArgumentException(
                    "수정할 플레이리스트를 찾을 수 없습니다."
            );
        }
    }
}
