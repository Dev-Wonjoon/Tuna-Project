package net.tuna.playlist.service;

import net.tuna.playlist.repository.PlaylistPostRepository;
import net.tuna.post.dto.PostDto;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public int removePosts(
            long playlistId,
            List<Long> postIds,
            long memberId
    ) {
        return playlistPostRepository.removeByPostIds(
                playlistId,
                postIds,
                memberId
        );
    }

    @Transactional
    public int removeAllPosts(
            long playlistId,
            long memberId
    ) {
        return playlistPostRepository.removeAll(
                playlistId,
                memberId
        );
    }
}
