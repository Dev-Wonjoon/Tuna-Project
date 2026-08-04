package net.tuna.music;

import java.util.Optional;

public interface MusicUrlResolver {
    Optional<MusicSource> resolve(String rawUrl);
}
