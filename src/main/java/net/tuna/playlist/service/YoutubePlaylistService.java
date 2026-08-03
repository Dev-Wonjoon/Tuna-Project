package net.tuna.playlist.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.tuna.playlist.cursor.CursorCodec;
import net.tuna.playlist.cursor.CursorDirection;
import net.tuna.playlist.cursor.CursorKey;
import net.tuna.playlist.cursor.CursorRequest;
import net.tuna.playlist.dto.CursorSlice;
import net.tuna.playlist.dto.PlaylistMusicCandidate;
import net.tuna.playlist.dto.YoutubeTrack;
import net.tuna.playlist.repository.PlaylistPostRepository;
import net.tuna.utils.YoutubeUrlParser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class YoutubePlaylistService {

    private static final int PAGE_SIZE = 10;
    private static final int CANDIDATE_SIZE = 10;

    private final PlaylistPostRepository playlistPostRepository;
    private final YoutubeUrlParser youtubeUrlParser;
    private final CursorCodec cursorCodec;

    public YoutubePlaylistService(
            PlaylistPostRepository repository,
            YoutubeUrlParser youtubeUrlParser,
            CursorCodec cursorCodec
    ) {
        this.playlistPostRepository = repository;
        this.youtubeUrlParser = youtubeUrlParser;
        this.cursorCodec = cursorCodec;
    }

    public CursorSlice<YoutubeTrack> getTracks(
            long playlistId,
            long memberId,
            String cursorToken
    ) {
        CursorRequest request = cursorCodec.decode(cursorToken);

        boolean firstRequest = request.isFirst();

        CursorDirection direction = firstRequest
                ? CursorDirection.NEXT
                : request.getDirection();

        CursorKey scanCursor = request.getKey();

        List<CursorItem> youtubeItems = collectYoutubeItems(
                playlistId,
                memberId,
                scanCursor,
                direction
        );

        boolean hasExtra = youtubeItems.size() > PAGE_SIZE;

        List<CursorItem> visibleItems = get
    }

    private List<CursorItem> collectYoutubeItems(
            long playlistId,
            long memberId,
            CursorKey initialCursor,
            CursorDirection direction
    ) {
        List<CursorItem> youtubeItems = new ArrayList<>();

        CursorKey scanCursor = initialCursor;

        while(youtubeItems.size() < PAGE_SIZE + 1) {
            List<PlaylistMusicCandidate> candidates =
                    playlistPostRepository.findYoutubeCandidate(
                            playlistId,
                            memberId,
                            scanCursor,
                            direction,
                            CANDIDATE_SIZE
                    );

            if(candidates.isEmpty()) {
                break;
            }

            for(PlaylistMusicCandidate candidate : candidates) {
                scanCursor = candidate.toCursorKey();

                youtubeUrlParser.extractVideoId(candidate.getMusicUrl())
                        .ifPresent(videoId -> youtubeItems.add(
                                new CursorItem(new YoutubeTrack(
                                        candidate.getPostId(),
                                        candidate.getTitle(),
                                        videoId
                                ),
                                        candidate.toCursorKey()
                        )));

                if(youtubeItems.size() == PAGE_SIZE + 1) {
                    break;
                }
            }

            if(candidates.size() < CANDIDATE_SIZE) {
                break;
            }
        }

        return youtubeItems;
    }

    private List<CursorItem> getVisibleItems(
            List<CursorItem> youtubeItems,
            CursorDirection direction
    ) {
        int endIndex = Math.min(
                PAGE_SIZE,
                youtubeItems.size()
        );

        List<CursorItem> visibleItems = new ArrayList<>(
                youtubeItems.subList(
                        0, endIndex
                )
        );

        if(direction == CursorDirection.PREVIOUS) {
            Collections.reverse(visibleItems);
        }

        return visibleItems;
    }

    @Getter
    @AllArgsConstructor
    private static class CursorItem {
        private final YoutubeTrack track;
        private final CursorKey cursorKey;
    }
}
