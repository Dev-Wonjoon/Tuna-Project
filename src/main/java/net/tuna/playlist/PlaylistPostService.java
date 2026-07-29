package net.tuna.playlist;

import net.tuna.playlist.repository.PlaylistPostRepository;
import net.tuna.post.dto.PostDto;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistPostService {

    private final PlaylistPostRepository playlistPostRepository;

    public PlaylistPostService(PlaylistPostRepository playlistPostRepository) {
        this.playlistPostRepository = playlistPostRepository;
    }

    public List<PostDto> getPosts(long playlistId, long memberId) {
        return playlistPostRepository.findAllByPlaylistId(
                playlistId,
                memberId
        );
    }

    public boolean addPost(long playlistId, long postId, long memberId) {
        try {
            int affectedRows = playlistPostRepository.add(playlistId, postId, memberId);

            return affectedRows == 1;
        } catch (DuplicateKeyException exception) {
            return false;
        }
    }

    public boolean removePost(long playlistId, long postId, long memberId) {
        int affectedRows = playlistPostRepository.remove(playlistId, postId, memberId);

        return affectedRows == 1;
    }
}
