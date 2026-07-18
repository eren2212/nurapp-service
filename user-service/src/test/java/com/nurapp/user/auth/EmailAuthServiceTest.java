package com.nurapp.user.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nurapp.user.domain.AuthIdentity;
import com.nurapp.user.domain.AuthIdentityRepository;

@ExtendWith(MockitoExtension.class)
class EmailAuthServiceTest {

	@Mock
	private AuthIdentityRepository identities;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private JwtService jwt;
	@Mock
	private RefreshTokenService refreshTokens;

	private EmailAuthService service;

	@BeforeEach
	void setUp() {
		service = new EmailAuthService(identities, passwordEncoder, jwt, refreshTokens);
	}

	@Test
	void upgradeSavesIdentityWithNormalizedEmailAndEncodedPassword() {
		UUID userId = UUID.randomUUID();
		when(identities.findByProviderAndProviderUid("email", "user@example.com")).thenReturn(Optional.empty());
		when(passwordEncoder.encode("plainPassword")).thenReturn("hashed-value");

		service.upgrade(userId, "  User@Example.com ", "plainPassword");

		verify(identities).save(argThatEmail("user@example.com", "hashed-value", userId));
	}

	@Test
	void upgradeThrowsWhenEmailAlreadyInUse() {
		when(identities.findByProviderAndProviderUid("email", "taken@example.com"))
				.thenReturn(Optional.of(AuthIdentity.email(UUID.randomUUID(), "taken@example.com", "hash")));

		assertThatThrownBy(() -> service.upgrade(UUID.randomUUID(), "taken@example.com", "pw"))
				.isInstanceOf(EmailAlreadyInUseException.class);
	}

	@Test
	void loginSucceedsAndReturnsTokenPair() {
		UUID userId = UUID.randomUUID();
		AuthIdentity identity = AuthIdentity.email(userId, "user@example.com", "hashed-value");
		when(identities.findByProviderAndProviderUid("email", "user@example.com")).thenReturn(Optional.of(identity));
		when(passwordEncoder.matches("plainPassword", "hashed-value")).thenReturn(true);
		when(jwt.issueAccessToken(userId)).thenReturn("access-token");
		when(refreshTokens.issue(userId)).thenReturn("refresh-token");

		TokenPairResponse result = service.login("user@example.com", "plainPassword");

		assertThat(result.accessToken()).isEqualTo("access-token");
		assertThat(result.refreshToken()).isEqualTo("refresh-token");
	}

	@Test
	void loginThrowsWhenIdentityNotFound() {
		when(identities.findByProviderAndProviderUid(eq("email"), any())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.login("nobody@example.com", "pw"))
				.isInstanceOf(InvalidCredentialsException.class);
	}

	@Test
	void loginThrowsWhenPasswordDoesNotMatch() {
		AuthIdentity identity = AuthIdentity.email(UUID.randomUUID(), "user@example.com", "hashed-value");
		when(identities.findByProviderAndProviderUid("email", "user@example.com")).thenReturn(Optional.of(identity));
		when(passwordEncoder.matches("wrongPassword", "hashed-value")).thenReturn(false);

		assertThatThrownBy(() -> service.login("user@example.com", "wrongPassword"))
				.isInstanceOf(InvalidCredentialsException.class);
	}

	@Test
	void loginThrowsWhenIdentityHasNoPasswordHash_deviceOnlyAccount() {
		// device-only kimlikte passwordHash null olur — email login denenirse reddedilmeli.
		AuthIdentity identity = AuthIdentity.email(UUID.randomUUID(), "user@example.com", null);
		when(identities.findByProviderAndProviderUid("email", "user@example.com")).thenReturn(Optional.of(identity));

		assertThatThrownBy(() -> service.login("user@example.com", "anyPassword"))
				.isInstanceOf(InvalidCredentialsException.class);
	}

	private static AuthIdentity argThatEmail(String expectedEmail, String expectedHash, UUID expectedUserId) {
		return org.mockito.ArgumentMatchers.argThat(a -> a != null
				&& expectedEmail.equals(a.getEmail())
				&& expectedHash.equals(a.getPasswordHash())
				&& expectedUserId.equals(a.getUserId()));
	}
}
