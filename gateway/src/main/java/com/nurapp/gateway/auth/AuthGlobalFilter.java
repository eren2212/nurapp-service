package com.nurapp.gateway.auth;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Kimlik doğrulamayı merkezi olarak gateway'de yapar.
 * - Korumalı yollarda geçerli JWT ister, yoksa 401 döner.
 * - Geçerli token'dan kullanıcı id'sini çıkarıp arka servislere güvenilir "X-User-Id" başlığıyla iletir.
 * - Client'ın gönderdiği her X-User-Id başlığını siler (spoofing önleme).
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String USER_HEADER = "X-User-Id";

    private final GatewayJwtService jwt;

    public AuthGlobalFilter(GatewayJwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String token = bearerToken(request);

        // Client'tan gelen X-User-Id her zaman temizlenir
        ServerHttpRequest.Builder builder = request.mutate().headers(h -> h.remove(USER_HEADER));

        if (isPublic(path)) {
            // Public yolda token varsa kimliği ekle, yoksa/geçersizse sorun etme
            if (token != null) {
                try {
                    builder.header(USER_HEADER, jwt.parseUserId(token).toString());
                } catch (Exception ignored) {
                    // public yolda geçersiz token yok sayılır
                }
            }
            return chain.filter(exchange.mutate().request(builder.build()).build());
        }

        // Korumalı yol: geçerli token zorunlu
        if (token == null) {
            return unauthorized(exchange);
        }
        try {
            UUID userId = jwt.parseUserId(token);
            builder.header(USER_HEADER, userId.toString());
            return chain.filter(exchange.mutate().request(builder.build()).build());
        } catch (Exception e) {
            return unauthorized(exchange);
        }
    }

    private boolean isPublic(String path) {
        return path.startsWith("/auth")
                || path.startsWith("/content")
                || path.startsWith("/actuator")
                || path.equals("/users/ping")
                || path.equals("/subscriptions/webhook");
    }

    private String bearerToken(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // yönlendirmeden önce çalışsın
    }
}
