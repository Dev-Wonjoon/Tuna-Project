package net.tuna.utils;

import org.springframework.stereotype.Component;

@Component
public class MusicThumbnailResolver {

    private static final String DEFAULT_IMAGE =
            "/img/tuna-note-blurple.png";

    private final YoutubeUrlParser youtubeUrlParser;

    public MusicThumbnailResolver(
            YoutubeUrlParser youtubeUrlParser
    ) {
        this.youtubeUrlParser = youtubeUrlParser;
    }

    public String resolve(String musicUrl) {
        return youtubeUrlParser.extractVideoId(musicUrl)
                .map(videoId -> "https://i.ytimg.com/vi/" + videoId + "/mqdefault.jpg")
                .orElse(DEFAULT_IMAGE);
    }
}
