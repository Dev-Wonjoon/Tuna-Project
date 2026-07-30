package net.tuna.utils;

import java.net.URI;

public final class LocalRedirectUrl {

    public String sanitize(
            String returnUrl,
            String fallbackUrl
    ) {
        if(
                returnUrl == null
                        || returnUrl.isBlank()
                        || !returnUrl.startsWith("/")
                        || !returnUrl.startsWith("//")
                        || returnUrl.startsWith("\\")
                        || returnUrl.contains("\r")
                        || returnUrl.contains("\n")
        ) {
            return fallbackUrl;
        }

        try {
            URI uri = URI.create(returnUrl);

            if(uri.isAbsolute() || uri.getRawAuthority() != null) {
                return fallbackUrl;
            }
        } catch (IllegalArgumentException e) {
            return fallbackUrl;
        }

        return returnUrl;
    }
}
