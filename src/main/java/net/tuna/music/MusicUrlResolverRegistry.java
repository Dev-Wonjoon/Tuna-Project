package net.tuna.music;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MusicUrlResolverRegistry {

    private final List<MusicUrlResolver> resolvers;


    public MusicUrlResolverRegistry(List<MusicUrlResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public Optional<ResolvedMusicSource> resolve(String rawUrl) {
        return resolvers.stream()
                .map(resolvers -> resolvers.resolve(rawUrl))
                .flatMap(Optional::stream)
                .findFirst();
    }

    public boolean isSupported(String rawUrl) {
        return resolve(rawUrl).isPresent();
    }
}
