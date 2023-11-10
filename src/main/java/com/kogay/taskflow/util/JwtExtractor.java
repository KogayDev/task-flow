package com.kogay.taskflow.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class JwtExtractor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public Optional<String> extract(HttpServletRequest request) {
        String token = extractTokenWithoutBearerPrefix(request);
        return Optional.ofNullable(token);
    }

    private static String extractTokenWithoutBearerPrefix(HttpServletRequest request) {
        String tokenWithBearerPrefix = request.getHeader(AUTHORIZATION_HEADER);
        if (tokenWithBearerPrefix != null && tokenWithBearerPrefix.startsWith(BEARER_PREFIX)) {
            return tokenWithBearerPrefix.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
