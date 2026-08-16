package net.tuna.music;

public record ResolvedMusicSource(
        String provider,
        String resourceType,
        String resourceId,
        String canonicalUrl
) {
}
