package net.tuna.playlist.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class YoutubeTrack {
    private final long postId;
    private final String title;
    private final String videoId;
}
