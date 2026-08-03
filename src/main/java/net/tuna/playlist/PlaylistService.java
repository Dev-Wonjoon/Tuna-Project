package net.tuna.playlist;

import net.tuna.playlist.dto.Playlist;
import net.tuna.playlist.repository.PlaylistPostRepository;
import net.tuna.playlist.repository.PlaylistRepository;
import net.tuna.post.dto.PostDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistPostRepository playlistPostRepository;

    private static final int MAX_PLAYLIST_LIMIT = 10;

    private static final List<String> PLAYLIST_IMAGE_URLS = List.of(
            "/img/tuna-note-blurple.png",
            "/img/tuna-note-coral.png",
            "/img/tuna-note-gold.png",
            "/img/tuna-note-mint.png",
            "/img/tuna-note-pink.png",
            "/img/tuna-note-sky-blue.png"
    );

    public PlaylistService(
            PlaylistRepository playlistRepository,
            PlaylistPostRepository playlistPostRepository
    ) {
        this.playlistRepository = playlistRepository;
        this.playlistPostRepository = playlistPostRepository;
    }

    @Transactional
    public long createPlaylist(Playlist playlist) {
        return createPlaylistInternal(playlist);
    }

    @Transactional
    public long createPlaylistWithPost(
            Playlist playlist,
            long postId
    ) {
        long playlistId = createPlaylistInternal(playlist);

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

    public void deletePlaylist(long playlistId, long memberId) {
        int affectedRows = playlistRepository.deleteById(playlistId, memberId);

        if(affectedRows != 1) {
            throw new IllegalArgumentException(
                    "삭제할 플레이리스트가 없습니다."
            );
        }
    }

    private long createPlaylistInternal(Playlist playlist) {
        int index = (int) (Math.random() * PLAYLIST_IMAGE_URLS.size());

        long memberId = playlist.getMemberId();

        playlistRepository.lockMember(memberId);

        if(playlistRepository.countByMemberId(memberId) >= MAX_PLAYLIST_LIMIT) {
            throw new IllegalArgumentException("플레이리스트는 최대 10개까지 만들 수 있습니다.");
        }

        playlist.setName(playlist.getName().trim());
        playlist.setImageUrl(getRandomImageUrl());

        return playlistRepository.save(playlist);
    }

    private String getRandomImageUrl() {
        return PLAYLIST_IMAGE_URLS.get((int)(Math.random() * PLAYLIST_IMAGE_URLS.size()));
    }
}
