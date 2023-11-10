package com.kogay.taskflow.filter;

import com.kogay.taskflow.dto.CustomUserDetails;
import com.kogay.taskflow.dto.ErrorResponse;
import com.kogay.taskflow.entity.User;
import com.kogay.taskflow.exception.AuthenticationUserNotFoundException;
import com.kogay.taskflow.exception.TokenIsNotValidOrPresentedException;
import com.kogay.taskflow.repository.UserRepository;
import com.kogay.taskflow.service.JwtProvider;
import com.kogay.taskflow.util.JsonResponseUtil;
import com.kogay.taskflow.util.JwtExtractor;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;
    private final RequestMatcher publicPaths = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/v1/auth/**"),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/swagger-ui*/**")
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            if (shouldAuthenticate(request)) {
                String token = JwtExtractor.extract(request)
                        .orElseThrow(TokenIsNotValidOrPresentedException::new);
                authenticateIfUserExists(token);
            }

            filterChain.doFilter(request, response);
        } catch (JwtException | AuthenticationUserNotFoundException ex) {
            JsonResponseUtil.send(
                    response,
                    ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), ex.getMessage())
            );
        }
    }

    private boolean shouldAuthenticate(HttpServletRequest request) {
        boolean isRequiredAuthPath = !publicPaths.matches(request);
        boolean isAuthenticationEmpty = SecurityContextHolder.getContext().getAuthentication() == null;
        return isRequiredAuthPath && isAuthenticationEmpty;
    }

    private void authenticateIfUserExists(String token) {
        String username = jwtProvider.getUsername(token);

        Optional<User> maybeUser = userRepository.findByUsername(username);
        if (maybeUser.isPresent()) {
            authenticate(maybeUser.get());
        } else {
            throw new AuthenticationUserNotFoundException(username);
        }
    }

    private static void authenticate(User user) {
        var roles = Collections.singleton(user.getRole());
        var principal = new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                "",
                roles
        );

        var uasToken = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                roles);

        SecurityContextHolder.getContext().setAuthentication(uasToken);
    }
}
