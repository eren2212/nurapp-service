package com.nurapp.user.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Kimlik doğrulaması gateway'de yapılır; gateway güvenilir "X-User-Id" başlığını ekler.
 * Bu filtre o başlığı okuyup SecurityContext'e koyar. Token doğrulama artık burada yapılmaz.
 */
@Component
public class HeaderAuthFilter extends OncePerRequestFilter {

    private static final String USER_HEADER = "X-User-Id";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String userId = request.getHeader(USER_HEADER);
        if (userId != null && !userId.isBlank()) {
            try {
                var auth = new UsernamePasswordAuthenticationToken(
                        UUID.fromString(userId), null, AuthorityUtils.NO_AUTHORITIES);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
