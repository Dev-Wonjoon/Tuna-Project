package net.tuna.playlist;

import jakarta.validation.Valid;
import net.tuna.member.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }


    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("title", "플레이리스트 추가");
        model.addAttribute("playlist", new Playlist());
        model.addAttribute("currentMenu", null);
        model.addAttribute("currentPlaylistId", null);

        return "pages/playlist-create";
    }

    @PostMapping("/new")
    public String createPlaylist(
            @Valid @ModelAttribute("playlist") Playlist playlist,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("title", "플레이리스트 추가");
            model.addAttribute("currentMenu", null);
            model.addAttribute("currentPlaylistId", null);

            return "pages/playlist-create";
        }

        long memberId = userDetails.getMember().getId();
        playlist.setMemberId(memberId);
        playlistService.createPlaylist(playlist);

        return "redirect:/";
    }
}
