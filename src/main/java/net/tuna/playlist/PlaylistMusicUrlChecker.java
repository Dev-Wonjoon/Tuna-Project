package net.tuna.playlist;

import net.tuna.music.MusicUrlResolverRegistry;
import net.tuna.playlist.repository.PlaylistPostRepository;
import org.springframework.stereotype.Component;

@Component
public class PlaylistMusicUrlChecker {

    private final PlaylistPostRepository playlistPostRepository;
    private final MusicUrlResolverRegistry resolverRegistry;


    public PlaylistMusicUrlChecker(PlaylistPostRepository playlistPostRepository, MusicUrlResolverRegistry resolverRegistry) {
        this.playlistPostRepository = playlistPostRepository;
        this.resolverRegistry = resolverRegistry;
    }

    public void validate(long postId) {
        String musicUrl = playlistPostRepository
                .findMusicUrlByPostId(postId)
                .orElseThrow(() -> new UnsupportedPlaylistMusicUrlException("음악 URL이 있는 게시글만 추가할 수 있습니다."));

        if(!resolverRegistry.isSupported(musicUrl)) {
            throw new UnsupportedPlaylistMusicUrlException("현재 Youtube 음악만 플레이리스트에 추가할 수 있습니다.");
        }
    }
}
