package net.rose.rose_menu_service.core.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.rose.rose_menu_service.core.config.security.CurrentUser;
import net.rose.rose_menu_service.core.config.security.client.IdentityAuthClient;
import net.rose.rose_menu_service.core.config.security.client.IdentityCurrentUserResponse;
import net.rose.rose_menu_service.core.persistence.id.UserId;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final IdentityAuthClient identityAuthClient;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null
                || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(
                BEARER_PREFIX.length()
        );

        try {
            IdentityCurrentUserResponse identityUser =
                    identityAuthClient.getCurrentUser(token);

            CurrentUser currentUser =
                    new CurrentUser(
                            UserId.of(identityUser.id()),
                            identityUser.email()
                    );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            currentUser,
                            null,
                            List.of()
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        } catch (RuntimeException exception) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}