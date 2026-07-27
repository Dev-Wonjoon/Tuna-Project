package net.tuna.playlist;

import net.tuna.member.dto.MemberDto;
import net.tuna.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class PlaylistService {

    private final MemberRepository memberRepository;
    private final PlaylistRepository playlistRepository;

    public PlaylistService(
            PlaylistRepository playlistRepository,
            MemberRepository memberRepository
    ) {
        this.memberRepository = memberRepository;
        this.playlistRepository = playlistRepository;
    }

    public void createPlaylist(Playlist playlist) {
        playlistRepository.save(playlist);
    }
}
