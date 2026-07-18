package com.nurapp.gateway.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Kimlik doğrulama uç noktalarında (özellikle /auth/login) brute-force denemelerini
 * yavaşlatmak için basit, bellek-içi sabit-pencere rate limiter.
 *
 * Tek gateway instance'ı için yeterli (bu repo'da yatay ölçeklenmiyor — docker-compose'da
 * tek container). Birden fazla instance'a çıkılırsa Redis tabanlı bir limiter'a taşınmalı.
 */
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

	/** Yalnızca bu tam yollarda uygulanır — diğer /auth/** uçları etkilenmez. */
	private static final Set<String> LIMITED_PATHS = Set.of("/auth/login", "/auth/device");

	private static final int MAX_REQUESTS_PER_WINDOW = 20;
	private static final Duration WINDOW = Duration.ofMinutes(1);

	private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
	private final Supplier<Instant> clock;

	public RateLimitFilter() {
		this(Instant::now);
	}

	/** Testlerde gerçek saati beklemeden pencere geçişini simüle etmek için. */
	RateLimitFilter(Supplier<Instant> clock) {
		this.clock = clock;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getURI().getPath();
		if (!LIMITED_PATHS.contains(path)) {
			return chain.filter(exchange);
		}

		String key = clientIp(exchange.getRequest()) + ":" + path;
		if (!tryConsume(key)) {
			exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
			exchange.getResponse().getHeaders().add(HttpHeaders.RETRY_AFTER, String.valueOf(WINDOW.toSeconds()));
			return exchange.getResponse().setComplete();
		}
		return chain.filter(exchange);
	}

	private boolean tryConsume(String key) {
		Instant now = clock.get();
		Window window = windows.compute(key, (k, existing) -> {
			if (existing == null || existing.isExpired(now)) {
				return new Window(now);
			}
			return existing;
		});
		return window.count.incrementAndGet() <= MAX_REQUESTS_PER_WINDOW;
	}

	private String clientIp(ServerHttpRequest request) {
		// cloudflared/proxy arkasında gerçek istemci IP'si X-Forwarded-For'un ilk değeri.
		String forwarded = request.getHeaders().getFirst("X-Forwarded-For");
		if (forwarded != null && !forwarded.isBlank()) {
			return forwarded.split(",")[0].trim();
		}
		if (request.getRemoteAddress() != null && request.getRemoteAddress().getAddress() != null) {
			return request.getRemoteAddress().getAddress().getHostAddress();
		}
		return "unknown";
	}

	private static final class Window {
		private final Instant start;
		private final AtomicInteger count = new AtomicInteger(0);

		Window(Instant start) {
			this.start = start;
		}

		boolean isExpired(Instant now) {
			return Duration.between(start, now).compareTo(WINDOW) >= 0;
		}
	}

	@Override
	public int getOrder() {
		return -2; // AuthGlobalFilter'dan (-1) önce çalışsın — gereksiz JWT parse'ı atla
	}
}
