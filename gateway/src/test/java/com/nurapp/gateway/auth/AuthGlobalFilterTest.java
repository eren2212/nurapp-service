package com.nurapp.gateway.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * AuthGlobalFilter'ın public/korumalı yol ayrımını ve X-User-Id enjeksiyonunu
 * gerçek bir gateway/route çalıştırmadan, saf birim testle doğrular.
 */
@ExtendWith(MockitoExtension.class)
class AuthGlobalFilterTest {

	private static final String USER_HEADER = "X-User-Id";

	@Mock
	private GatewayJwtService jwt;
	@Mock
	private GatewayFilterChain chain;

	private AuthGlobalFilter filter;

	@BeforeEach
	void setUp() {
		filter = new AuthGlobalFilter(jwt);
	}

	@Test
	void protectedPathWithoutTokenIsRejected() {
		ServerWebExchange exchange = exchangeFor("/users/me", null);

		filter.filter(exchange, chain).block();

		assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void protectedPathWithInvalidTokenIsRejected() {
		when(jwt.parseUserId("bad-token")).thenThrow(new RuntimeException("invalid"));
		ServerWebExchange exchange = exchangeFor("/users/me", "bad-token");

		filter.filter(exchange, chain).block();

		assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void protectedPathWithValidTokenInjectsTrustedUserIdHeaderAndForwards() {
		UUID userId = UUID.randomUUID();
		when(jwt.parseUserId("good-token")).thenReturn(userId);
		when(chain.filter(any())).thenReturn(Mono.empty());
		ServerWebExchange exchange = exchangeFor("/users/me", "good-token");

		filter.filter(exchange, chain).block();

		ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
		verify(chain).filter(captor.capture());
		assertThat(captor.getValue().getRequest().getHeaders().getFirst(USER_HEADER)).isEqualTo(userId.toString());
	}

	@Test
	void clientSuppliedUserIdHeaderIsAlwaysStripped_spoofingPrevention() {
		UUID realUserId = UUID.randomUUID();
		when(jwt.parseUserId("good-token")).thenReturn(realUserId);
		when(chain.filter(any())).thenReturn(Mono.empty());
		ServerWebExchange exchange = MockServerWebExchange.from(
				MockServerHttpRequest.get("/users/me")
						.header("Authorization", "Bearer good-token")
						.header(USER_HEADER, "spoofed-attacker-id")
						.build());

		filter.filter(exchange, chain).block();

		ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
		verify(chain).filter(captor.capture());
		assertThat(captor.getValue().getRequest().getHeaders().getFirst(USER_HEADER)).isEqualTo(realUserId.toString());
	}

	@Test
	void publicPathWithoutTokenPassesThroughWithoutUserIdHeader() {
		when(chain.filter(any())).thenReturn(Mono.empty());
		ServerWebExchange exchange = exchangeFor("/auth/device", null);

		filter.filter(exchange, chain).block();

		assertThat(exchange.getResponse().getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
		ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
		verify(chain).filter(captor.capture());
		assertThat(captor.getValue().getRequest().getHeaders().getFirst(USER_HEADER)).isNull();
	}

	@Test
	void publicPathWithInvalidTokenIsIgnoredNotRejected() {
		when(jwt.parseUserId("bad-token")).thenThrow(new RuntimeException("invalid"));
		when(chain.filter(any())).thenReturn(Mono.empty());
		ServerWebExchange exchange = exchangeFor("/content/dhikr-programs", "bad-token");

		filter.filter(exchange, chain).block();

		assertThat(exchange.getResponse().getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void publicPathWithValidTokenStillInjectsUserIdForPersonalization() {
		UUID userId = UUID.randomUUID();
		when(jwt.parseUserId("good-token")).thenReturn(userId);
		when(chain.filter(any())).thenReturn(Mono.empty());
		ServerWebExchange exchange = exchangeFor("/content/dhikr-programs", "good-token");

		filter.filter(exchange, chain).block();

		ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
		verify(chain).filter(captor.capture());
		assertThat(captor.getValue().getRequest().getHeaders().getFirst(USER_HEADER)).isEqualTo(userId.toString());
	}

	@Test
	void authUpgradeIsProtectedDespiteAuthPrefix() {
		// /auth/** genelde public ama /auth/upgrade istisna — mevcut oturumun token'ı gerekir.
		ServerWebExchange exchange = exchangeFor("/auth/upgrade", null);

		filter.filter(exchange, chain).block();

		assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void publicUtilityPathsAreNotAuthRequired() {
		when(chain.filter(any())).thenReturn(Mono.empty());
		for (String path : new String[] { "/users/ping", "/subscriptions/webhook", "/actuator/health" }) {
			ServerWebExchange exchange = exchangeFor(path, null);
			filter.filter(exchange, chain).block();
			assertThat(exchange.getResponse().getStatusCode())
					.as("path %s should be public", path)
					.isNotEqualTo(HttpStatus.UNAUTHORIZED);
		}
	}

	private ServerWebExchange exchangeFor(String path, String bearerToken) {
		MockServerHttpRequest.BaseBuilder<?> builder = MockServerHttpRequest.get(path);
		if (bearerToken != null) {
			builder = builder.header("Authorization", "Bearer " + bearerToken);
		}
		return MockServerWebExchange.from(builder.build());
	}
}
