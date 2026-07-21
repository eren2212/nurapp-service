package com.nurapp.user.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nurapp.user.domain.RefreshToken;
import com.nurapp.user.domain.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

	@Mock
	private RefreshTokenRepository repo;

	private RefreshTokenService service;

	@BeforeEach
	void setUp() {
		service = new RefreshTokenService(repo, 30);
	}

	@Test
	void issueSavesHashedTokenAndReturnsRawValue() {
		UUID userId = UUID.randomUUID();

		String raw = service.issue(userId);

		assertThat(raw).isNotBlank();
		verify(repo).save(any(RefreshToken.class));
	}

	@Test
	void issueGeneratesDifferentTokensEachCall() {
		UUID userId = UUID.randomUUID();
		String first = service.issue(userId);
		String second = service.issue(userId);
		assertThat(first).isNotEqualTo(second);
	}

	@Test
	void consumeReturnsUserIdAndRevokesValidToken() {
		UUID userId = UUID.randomUUID();
		RefreshToken stored = RefreshToken.create(userId, "irrelevant-in-mock", OffsetDateTime.now().plusDays(1));
		when(repo.findByTokenHash(any())).thenReturn(Optional.of(stored));
		when(repo.revokeIfActive(any())).thenReturn(1); // atomik iptal 1 satır güncelledi (bu istek kazandı)

		UUID result = service.consume("some-raw-token");

		assertThat(result).isEqualTo(userId);
		verify(repo).revokeIfActive(any());
	}

	@Test
	void consumeThrowsWhenTokenUnknown() {
		when(repo.findByTokenHash(any())).thenReturn(Optional.empty());
		assertThatThrownBy(() -> service.consume("unknown-token"))
				.isInstanceOf(InvalidRefreshTokenException.class);
	}

	@Test
	void consumeThrowsWhenTokenExpired() {
		UUID userId = UUID.randomUUID();
		RefreshToken expired = RefreshToken.create(userId, "hash", OffsetDateTime.now().minusMinutes(1));
		when(repo.findByTokenHash(any())).thenReturn(Optional.of(expired));

		assertThatThrownBy(() -> service.consume("expired-token"))
				.isInstanceOf(InvalidRefreshTokenException.class);
	}

	@Test
	void consumeThrowsWhenTokenAlreadyRevoked() {
		UUID userId = UUID.randomUUID();
		RefreshToken revoked = RefreshToken.create(userId, "hash", OffsetDateTime.now().plusDays(1));
		when(repo.findByTokenHash(any())).thenReturn(Optional.of(revoked));
		when(repo.revokeIfActive(any())).thenReturn(0); // DB'de zaten revoked → 0 satır etkilenir

		assertThatThrownBy(() -> service.consume("revoked-token"))
				.isInstanceOf(InvalidRefreshTokenException.class);
	}

	@Test
	void consumeThrowsImmediatelyOnBlankToken() {
		assertThatThrownBy(() -> service.consume("  ")).isInstanceOf(InvalidRefreshTokenException.class);
		assertThatThrownBy(() -> service.consume(null)).isInstanceOf(InvalidRefreshTokenException.class);
		verify(repo, never()).findByTokenHash(any());
	}

	@Test
	void consumeIsNotReusable_secondConsumeWithSameRawTokenFailsOnceRevokedInStorage() {
		// Rotasyon: consume() token'ı DB'de revoked=true yapar. Aynı hash tekrar
		// sorgulandığında repo artık revoked=true dönmeli (burada mock ile simüle edilir).
		UUID userId = UUID.randomUUID();
		RefreshToken stored = RefreshToken.create(userId, "hash", OffsetDateTime.now().plusDays(1));
		when(repo.findByTokenHash(any())).thenReturn(Optional.of(stored));
		when(repo.revokeIfActive(any())).thenReturn(1, 0); // ilk consume kazanır (1), ikincisi 0 satır alır

		service.consume("raw-token");
		assertThatThrownBy(() -> service.consume("raw-token")).isInstanceOf(InvalidRefreshTokenException.class);
		verify(repo, times(2)).findByTokenHash(any());
	}
}
