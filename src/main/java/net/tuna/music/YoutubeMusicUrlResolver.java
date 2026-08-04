package net.tuna.music;

import net.tuna.utils.YoutubeUrlParser;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class YoutubeMusicUrlResolver implements MusicUrlResolver {

    private final YoutubeUrlParser youtubeUrlParser;

    public YoutubeMusicUrlResolver(YoutubeUrlParser youtubeUrlParser) {
        this.youtubeUrlParser = youtubeUrlParser;
    }


    @Override
    public Optional<MusicSource> resolve(String rawUrl) {
        return youtubeUrlParser.extractVideoId(rawUrl)
                .map(videoId -> new MusicSource("youtube", videoId));
    }
}
