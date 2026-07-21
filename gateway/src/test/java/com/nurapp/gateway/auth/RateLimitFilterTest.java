package com.nurapp.gateway.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

class RateLimitFilterTest {

	private AtomicReference<Instant> now;
	private RateLimitFilter filter;
	private GatewayFilterChain chain;

	@BeforeEach
	void setUp() {
		now = new AtomicReference<>(Instant.parse("2026-01-01T00:00:00Z"));
		filter = new RateLimitFilter(now::get);
		chain = mock(GatewayFilterChain.class);
		when(chain.filter(any())).thenReturn(Mono.empty());
	}

	@Test
	void requestsUnderLimitPassThrough() {
		for (int i = 0; i < 20; i++) {
			ServerWebExchange exchange = exchangeFor("/auth/login", "1.2.3.4");
			filter.filter(exchange, chain).block();
			assertThat(exchange.getResponse().getStatusCode())
					.as("request %d should pass", i + 1)
					.isNotEqualTo(HttpStatus.TOO_MANY_REQUESTS);
		}
	}

	@Test
	void requestOverLimitIsRejectedWith429() {
		for (int i = 0; i < 20; i++) {
			filter.filter(exchangeFor("/auth/login", "1.2.3.4"), chain).block();
		}
		ServerWebExchange exchange = exchangeFor("/auth/login", "1.2.3.4");

		filter.filter(exchange, chain).block();

		assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
		assertThat(exchange.getResponse().getHeaders().getFirst("Retry-After")).isNotNull();
	}

	@Test
	void deviceEndpointIsAlsoLimited() {
		for (int i = 0; i < 20; i++) {
			filter.filter(exchangeFor("/auth/device", "5.6.7.8"), chain).block();
		}
		ServerWebExchange exchange = exchangeFor("/auth/device", "5.6.7.8");

		filter.filter(exchange, chain).block();

		assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
	}

	@Test
	void unrelatedPathsAreNeverLimited() {
		for (int i = 0; i < 50; i++) {
			ServerWebExchange exchange = exchangeFor("/auth/token/refresh", "9.9.9.9");
			filter.filter(exchange, chain).block();
			assertThat(exchange.getResponse().getStatusCode()).isNotEqualTo(HttpStatus.TOO_MANY_REQUESTS);
		}
	}

	@Test
	void differentIpsHaveIndependentBudgets() {
		for (int i = 0; i < 20; i++) {
			filter.filter(exchangeFor("/auth/login", "1.1.1.1"), chain).block();
		}
		// 1.1.1.1 artık limitli, ama farklı bir IP hâlâ geçebilmeli.
		ServerWebExchange blocked = exchangeFor("/auth/login", "1.1.1.1");
		filter.filter(blocked, chain).block();
		ServerWebExchange other = exchangeFor("/auth/login", "2.2.2.2");
		filter.filter(other, chain).block();

		assertThat(blocked.getResponse().getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
		assertThat(other.getResponse().getStatusCode()).isNotEqualTo(HttpStatus.TOO_MANY_REQUESTS);
	}

	@Test
	void windowResetsAfterExpiry() {
		for (int i = 0; i < 20; i++) {
			filter.filter(exchangeFor("/auth/login", "3.3.3.3"), chain).block();
		}
		ServerWebExchange stillBlocked = exchangeFor("/auth/login", "3.3.3.3");
		filter.filter(stillBlocked, chain).block();
		assertThat(stillBlocked.getResponse().getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);

		now.set(now.get().plusSeconds(61)); // pencere (1 dk) geçti

		ServerWebExchange afterReset = exchangeFor("/auth/login", "3.3.3.3");
		filter.filter(afterReset, chain).block();
		assertThat(afterReset.getResponse().getStatusCode()).isNotEqualTo(HttpStatus.TOO_MANY_REQUESTS);
	}

	@Test
	void usesCfConnectingIpOverRawRemoteAddress() {
		// Cloudflare arkasında gerçek istemci IP'si CF-Connecting-IP header'ında gelir; TCP peer
		// (remoteAddress) tüm istekler için aynı cloudflared IP'sidir, ona göre saymamalıyız.
		for (int i = 0; i < 20; i++) {
			ServerWebExchange exchange = MockServerWebExchange.from(
					MockServerHttpRequest.post("/auth/login")
							.header("CF-Connecting-IP", "8.8.8.8")
							.remoteAddress(new InetSocketAddress("10.0.0.1", 12345))
							.build());
			filter.filter(exchange, chain).block();
		}
		ServerWebExchange exchange = MockServerWebExchange.from(
				MockServerHttpRequest.post("/auth/login")
						.header("CF-Connecting-IP", "8.8.8.8")
						.remoteAddress(new InetSocketAddress("10.0.0.1", 12345))
						.build());

		filter.filter(exchange, chain).block();

		assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
	}

	@Test
	void spoofedXForwardedForCannotBypassLimit() {
		// GÜVENLİK regresyonu: saldırgan her istekte farklı bir X-Forwarded-For gönderse bile
		// gerçek IP (CF-Connecting-IP / remoteAddress) sabit kaldığından limit yenilenmemeli.
		for (int i = 0; i < 20; i++) {
			ServerWebExchange exchange = MockServerWebExchange.from(
					MockServerHttpRequest.post("/auth/login")
							.header("X-Forwarded-For", "1.2.3." + i) // her istekte farklı, uydurma
							.header("CF-Connecting-IP", "9.9.9.9")
							.remoteAddress(new InetSocketAddress("10.0.0.1", 12345))
							.build());
			filter.filter(exchange, chain).block();
		}
		ServerWebExchange exchange = MockServerWebExchange.from(
				MockServerHttpRequest.post("/auth/login")
						.header("X-Forwarded-For", "1.2.3.99")
						.header("CF-Connecting-IP", "9.9.9.9")
						.remoteAddress(new InetSocketAddress("10.0.0.1", 12345))
						.build());

		filter.filter(exchange, chain).block();

		assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
	}

	private ServerWebExchange exchangeFor(String path, String remoteIp) {
		return MockServerWebExchange.from(
				MockServerHttpRequest.post(path)
						.remoteAddress(new InetSocketAddress(remoteIp, 54321))
						.build());
	}
}
