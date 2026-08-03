package net.tuna.playlist;

import net.tuna.member.security.CustomUserDetails;
import net.tuna.playlist.dto.Playlist;
import net.tuna.playlist.service.PlaylistService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class PlaylistModelAdvice {

    private final PlaylistService playlistService;

    public PlaylistModelAdvice(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @ModelAttribute("playlists")
    public List<Playlist> playlists(
            @AuthenticationPrincipal
            CustomUserDetails userDetails
    ) {
        if(userDetails == null) {
            return List.of();
        }

        long memberId =
                userDetails.getMember().getId();

        return playlistService.getPlaylists(memberId);
    }
}
