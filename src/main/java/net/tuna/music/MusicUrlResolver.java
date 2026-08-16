package net.tuna.music;

import java.util.Optional;

public interface MusicUrlResolver {
    Optional<ResolvedMusicSource> resolve(String rawUrl);
}
