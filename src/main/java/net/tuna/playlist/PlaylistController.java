package net.tuna.playlist;

import jakarta.validation.Valid;
import net.tuna.member.security.CustomUserDetails;
import net.tuna.playlist.dto.Playlist;
import net.tuna.playlist.dto.PlaylistNameUpdateDto;
import net.tuna.post.dto.PostDetailResponse;
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

        List<PostDetailResponse> posts = playlistPostService
                .getPosts(playlistId, memberId)
                .stream()
                .map(PostDetailResponse::from)
                .toList();

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

        redirectAttributes.addFlashAttribute(
                "playlistCreateError",
                errorMessage
        );

        return "redirect:" + safeReturnUrl;
    }

    @PostMapping("/{playlistId}/posts/{postId}/delete")
    public String removePost(
            @PathVariable("playlistId") long playlistId,
            @PathVariable("postId") long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        long memberId = userDetails.getMember().getId();

        boolean removed = playlistPostService.removePost(
                playlistId,
                postId,
                memberId
        );

        if(removed) {
            redirectAttributes.addFlashAttribute(
                    "playlistEditSuccess",
                    "곡을 플레이리스트에서 제거했습니다."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "playlistEditError",
                    "플레이리스트가 이미 비어 있습니다."
            );
        }

        return "redirect:/playlists/" + playlistId + "/edit";
    }

    @PostMapping("/{playlistId}/posts/delete-selected")
    public String removeSelectedPosts(
            @PathVariable("playlistId") long playlistId,
            @RequestParam(name = "postIds", required = false)
            List<Long> postIds,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        if(postIds == null || postIds.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "playlistEditError",
                    "삭제할 곡을 선택해주세요."
            );

            return "redirect:/playlists/" + playlistId + "/edit";
        }

        long memberId = userDetails.getMember().getId();

        int removedCount = playlistPostService.removePosts(
                playlistId,
                postIds,
                memberId
        );

        if(removedCount > 0) {
            redirectAttributes.addFlashAttribute(
                    "playlistEditSuccess",
                    removedCount + "곡을 플레이리스트에서 제거했습니다."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "playlistEditError",
                    "선택한 곡을 찾을 수 없습니다."
            );
        }

        return "redirect:/playlists/" + playlistId + "/edit";
    }

    @PostMapping("/{playlistId}/posts/delete-all")
    public String removeAllPosts(
            @PathVariable("playlistId") long playlistId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        long memberId = userDetails.getMember().getId();

        int removedCount = playlistPostService.removeAllPosts(
                playlistId,
                memberId
        );

        if(removedCount > 0) {
            redirectAttributes.addFlashAttribute(
                    "playlistEditSuccess",
                    "플레이리스트의 모든 곡을 제거했습니다."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "playlistEditError",
                    "플레이리스트가 이미 있습니다."
            );
        }

        return "redirect:/playlists/" + playlistId + "/edit";
    }

    @GetMapping("/{playlistId}/edit")
    public String playlistEdit(
            @PathVariable("playlistId") long playlistId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
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

        model.addAttribute(
                "title",
                playlist.getName() + " 수정"
        );

        model.addAttribute("playlist", playlist);

        model.addAttribute("playlistForm", new PlaylistNameUpdateDto(playlist.getName()));

        model.addAttribute("posts", posts);
        model.addAttribute("currentMenu", null);
        model.addAttribute("currentPlaylistId", playlistId);

        return "pages/playlist-edit";
    }

    @PostMapping("/{playlistId}/edit")
    public String updatePlaylistName(
            @PathVariable("playlistId") long playlistId,
            @Valid @ModelAttribute("playlistForm")
            PlaylistNameUpdateDto playlistForm,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        long memberId = userDetails.getMember().getId();

        if(!bindingResult.hasErrors()) {
            try {
                playlistService.updatePlaylistName(
                        playlistId,
                        memberId,
                        playlistForm.getName()
                );
            } catch (DuplicateKeyException exception) {
                bindingResult.rejectValue(
                        "name",
                        "duplicate",
                        "같은 이름의 플레이리스트가 이미 있습니다."
                );
            }
        }

        if(bindingResult.hasErrors()) {
            Playlist playlist = playlistService.getPlaylistById(
                    playlistId,
                    memberId
            );

            List<PostDto> posts = playlistPostService.getPosts(
                    playlistId,
                    memberId
            );

            model.addAttribute(
                    "title",
                    playlist.getName() + "수정"
            );
            model.addAttribute("playlist", playlist);
            model.addAttribute("posts", posts);
            model.addAttribute("currentMenu", null);
            model.addAttribute("currentPlaylistId", playlistId);

            return "pages/playlist-edit";
        }

        redirectAttributes.addFlashAttribute(
                "playlistEditSuccess",
                "플레이리스트 이름을 변경했습니다."
        );

        return "redirect:/playlists/" + playlistId + "/edit";
    }

    @PostMapping("/{playlistId}/delete")
    public String deletePlaylist(
            @PathVariable long playlistId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        long memberId = userDetails.getMember().getId();

        playlistService.deletePlaylist(playlistId, memberId);

        redirectAttributes.addFlashAttribute(
                "playlistMessage",
                "플레이리스트를 삭제했습니다."
        );

        return "redirect:/";
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
