package net.tuna.playlist;

import jakarta.validation.Valid;
import net.tuna.member.security.CustomUserDetails;
import net.tuna.post.dto.PostDto;
import net.tuna.utils.LocalRedirectUrl;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final PlaylistPostService playlistPostService;

    public PlaylistController(
            PlaylistService playlistService,
            PlaylistPostService playlistPostService
    ) {
        this.playlistService = playlistService;
        this.playlistPostService = playlistPostService;
    }

    @PostMapping
    public String createPlaylist(
            @Valid @ModelAttribute("playlist")
            Playlist playlist,
            BindingResult bindingResult,
            @RequestParam(required = false)
            Long postId,
            @RequestParam(defaultValue = "/")
            String returnUrl,
            @AuthenticationPrincipal
            CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        if(bindingResult.hasErrors()) {
            return redirectWithCreateError(
                    playlist,
                    postId,
                    returnUrl,
                    getValidationMessage(bindingResult),
                    redirectAttributes
            );
        }

        long memberId = userDetails.getMember().getId();

        playlist.setMemberId(memberId);

        try {
            long playlistId;

            if(postId == null) {
                playlistId = playlistService.createPlaylist(playlist);
            } else {
                playlistId = playlistService.createPlaylistWithPost(
                        playlist,
                        postId
                );
            }

            return "redirect:/playlists/" + playlistId;
        } catch (DuplicateKeyException exception) {
            return redirectWithCreateError(
                    playlist,
                    postId,
                    returnUrl,
                    "같은 이름의 플레이리스트가 이미 있습니다.",
                    redirectAttributes
            );
        } catch (IllegalArgumentException exception) {
            return redirectWithCreateError(
                    playlist,
                    postId,
                    returnUrl,
                    exception.getMessage(),
                    redirectAttributes
            );
        }
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

    private String redirectWithCreateError(
            Playlist playlist,
            Long postId,
            String returnUrl,
            String errorMessage,
            RedirectAttributes redirectAttributes
    ) {
        String safeReturnUrl = LocalRedirectUrl.sanitize(returnUrl, "/");

        redirectAttributes.addFlashAttribute("playlistCreateOpen", true);

        redirectAttributes.addFlashAttribute(
                "playlistCreateName",
                playlist.getName() == null
                        ? ""
                        : playlist.getName()
        );

        if(postId != null) {
            redirectAttributes.addFlashAttribute(
                    "playlistCreatePostId",
                    postId
            );
        }

        redirectAttributes.addAttribute(
                "playlistCreateReturnUrl",
                safeReturnUrl
        );

        redirectAttributes.addAttribute(
                "playlistCreateError",
                errorMessage
        );

        return "redirect:" + safeReturnUrl;
    }

    private String getValidationMessage(
            BindingResult bindingResult
    ) {
        FieldError nameError = bindingResult.getFieldError("name");

        if(nameError == null || nameError.getDefaultMessage() == null) {
            return "입력값을 확인해주세요.";
        }

        return nameError.getDefaultMessage();
    }
}
