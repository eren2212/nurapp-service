package com.nurapp.user.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class JwtServiceTest {

	private static final String SECRET = "unit-test-secret-key-please-ignore-1234567890-abcdefghijklmno";

	private final JwtService jwt = new JwtService(SECRET, 15);

	@Test
	void issuedTokenRoundTripsToSameUserId() {
		UUID userId = UUID.randomUUID();
		String token = jwt.issueAccessToken(userId);
		assertThat(jwt.parseUserId(token)).isEqualTo(userId);
	}

	@Test
	void rejectsTamperedToken() {
		String token = jwt.issueAccessToken(UUID.randomUUID());
		String tampered = token.substring(0, token.length() - 2) + "xx";
		assertThatThrownBy(() -> jwt.parseUserId(tampered)).isInstanceOf(RuntimeException.class);
	}

	@Test
	void rejectsTokenSignedWithDifferentSecret() {
		JwtService other = new JwtService("another-completely-different-secret-key-1234567890-abcdefgh", 15);
		String token = other.issueAccessToken(UUID.randomUUID());
		assertThatThrownBy(() -> jwt.parseUserId(token)).isInstanceOf(RuntimeException.class);
	}

	@Test
	void rejectsGarbageToken() {
		assertThatThrownBy(() -> jwt.parseUserId("not-a-jwt")).isInstanceOf(RuntimeException.class);
	}
}
