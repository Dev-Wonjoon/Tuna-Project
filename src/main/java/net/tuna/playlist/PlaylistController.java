package net.tuna.playlist;

import jakarta.validation.Valid;
import net.tuna.member.security.CustomUserDetails;
import net.tuna.post.dto.PostDto;
import net.tuna.post.service.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final PlaylistPostService playlistPostService;
    private final PostService postService;

    public PlaylistController(
            PlaylistService playlistService,
            PlaylistPostService playlistPostService,
            PostService postService
    ) {
        this.playlistService = playlistService;
        this.playlistPostService = playlistPostService;
        this.postService = postService;
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

    @GetMapping("/{playlistId}")
    public String playlistDetail(
            @PathVariable long playlistId,
            @AuthenticationPrincipal
            CustomUserDetails userDetails,
            Model model
    ) {
        long memberId = userDetails.getMember().getId();

        Playlist playlist = playlistService.getPlaylistById(
                playlistId,
                memberId
        );

        List<PostDto> posts = playlistPostService.getPosts(
                playlistId,
                memberId
        );

        model.addAttribute("title", playlist.getName());
        model.addAttribute("playlist", playlist);
        model.addAttribute("posts", posts);
        model.addAttribute("currentMenu", null);
        model.addAttribute("currentPlaylistId", playlistId);

        return "pages/playlist-detail";
    }

    @PostMapping("/posts")
    public String addPost(
            @RequestParam long playlistId,
            @RequestParam long postId,
            @AuthenticationPrincipal
            CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        long memberId = userDetails.getMember().getId();

        boolean added = playlistPostService.addPost(
                playlistId,
                postId,
                memberId
        );

        redirectAttributes.addFlashAttribute(
                "playlistMessage",
                added
                        ? "플레이리스트에 추가되었습니다."
                        : "이미 추가된 게시글입니다."
        );

        if(!added) {
            redirectAttributes.addFlashAttribute(
                    "playlistAlert",
                    "이미 이 플레이리스트에 추가된 노래입니다."
            );
        }

        return "redirect:/";
    }
}
