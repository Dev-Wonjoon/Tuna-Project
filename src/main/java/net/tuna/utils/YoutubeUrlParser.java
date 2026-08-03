package net.tuna.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class YoutubeUrlParser {

    private static final Pattern VIDEO_ID_PATTERN =
            Pattern.compile("^[A-Za-z0-9_-]{11}$");

    private static final Pattern YOUTUBE_PATH_PATTERN =
            Pattern.compile("^/(?:shorts|embed|live)/([^/?#]+)");

    private static final Pattern NOCOOKIE_PATH_PATTERN =
            Pattern.compile("^/embed/([^/?#]+)");

    public Optional<String> extractVideoId(String rawUrl) {
        if(rawUrl == null || rawUrl.isBlank()) {
            return Optional.empty();
        }

        try {
            URI uri = URI.create(rawUrl.trim());

            if(!isHttp(uri.getScheme())) {
                return Optional.empty();
            }

            if(uri.getHost() == null) {
                return Optional.empty();
            }

            String host = uri.getHost()
                    .toLowerCase(Locale.ROOT)
                    .replaceFirst("^www\\.", "");

            String videoId = switch(host) {
                case "youtu.be" -> extractFirstPathSegment(uri.getPath());

                case "youtube.com",
                     "m.youtube.com",
                     "music.youtube.com" -> extractYoutubeVideoId(uri);

                case "youtube-nocookie.com" ->
                    extractFromPath(uri.getPath(), NOCOOKIE_PATH_PATTERN);

                default -> null;
            };

            if(videoId == null || !VIDEO_ID_PATTERN.matcher(videoId).matches()) {
                return Optional.empty();
            }

            return Optional.of(videoId);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private boolean isHttp(String scheme) {
        return "https".equalsIgnoreCase(scheme)
                || "http".equalsIgnoreCase(scheme);
    }

    private String extractYoutubeVideoId(URI uri) {
        if("/watch".equals(uri.getPath())) {
            return UriComponentsBuilder
                    .fromUri(uri)
                    .build()
                    .getQueryParams()
                    .getFirst("v");
        }

        return extractFromPath(uri.getPath(), YOUTUBE_PATH_PATTERN);
    }

    private String extractFromPath(String path, Pattern pattern) {
        if(path == null) {
            return null;
        }

        Matcher matcher = pattern.matcher(path);

        return matcher.find()
                ? matcher.group(1)
                : null;
    }

    private String extractFirstPathSegment(String path) {
        if(path == null || path.isBlank()) {
            return null;
        }

        for(String segment : path.split("/")) {
            if(!segment.isBlank()) {
                return segment;
            }
        }

        return null;
    }
}
