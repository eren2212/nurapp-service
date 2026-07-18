package com.nurapp.user.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nurapp.user.domain.AuthIdentity;
import com.nurapp.user.domain.AuthIdentityRepository;
import com.nurapp.user.domain.User;
import com.nurapp.user.domain.UserRepository;

@ExtendWith(MockitoExtension.class)
class DeviceAuthServiceTest {

	@Mock
	private UserRepository users;
	@Mock
	private AuthIdentityRepository identities;
	@Mock
	private JwtService jwt;
	@Mock
	private RefreshTokenService refreshTokens;

	private DeviceAuthService service;

	@BeforeEach
	void setUp() {
		service = new DeviceAuthService(users, identities, jwt, refreshTokens);
		when(jwt.issueAccessToken(any())).thenReturn("access-token");
		when(refreshTokens.issue(any())).thenReturn("refresh-token");
	}

	@Test
	void newDeviceIdCreatesUserAndIdentity() {
		UUID newUserId = UUID.randomUUID();
		User saved = mock(User.class);
		when(saved.getId()).thenReturn(newUserId);
		when(identities.findByProviderAndProviderUid("device", "device-1")).thenReturn(Optional.empty());
		when(users.save(any(User.class))).thenReturn(saved);

		DeviceRegisterResponse response = service.registerDevice("device-1");

		assertThat(response.created()).isTrue();
		assertThat(response.userId()).isEqualTo(newUserId);
		assertThat(response.deviceId()).isEqualTo("device-1");
		assertThat(response.accessToken()).isEqualTo("access-token");
		assertThat(response.refreshToken()).isEqualTo("refresh-token");
		verify(identities).save(any(AuthIdentity.class));
	}

	@Test
	void repeatedDeviceIdReturnsSameUserId_idempotent() {
		UUID existingUserId = UUID.randomUUID();
		AuthIdentity existing = AuthIdentity.device(existingUserId, "device-2");
		when(identities.findByProviderAndProviderUid("device", "device-2")).thenReturn(Optional.of(existing));

		DeviceRegisterResponse response = service.registerDevice("device-2");

		assertThat(response.created()).isFalse();
		assertThat(response.userId()).isEqualTo(existingUserId);
		verify(users, never()).save(any());
		verify(identities, never()).save(any());
	}

	@Test
	void blankDeviceIdGeneratesRandomOne() {
		UUID newUserId = UUID.randomUUID();
		User saved = mock(User.class);
		when(saved.getId()).thenReturn(newUserId);
		when(identities.findByProviderAndProviderUid(eq("device"), any())).thenReturn(Optional.empty());
		when(users.save(any(User.class))).thenReturn(saved);

		DeviceRegisterResponse response = service.registerDevice(null);

		assertThat(response.deviceId()).isNotBlank();
		assertThat(response.created()).isTrue();
	}

	@Test
	void deviceIdIsTrimmedBeforeLookup() {
		UUID existingUserId = UUID.randomUUID();
		AuthIdentity existing = AuthIdentity.device(existingUserId, "device-3");
		when(identities.findByProviderAndProviderUid("device", "device-3")).thenReturn(Optional.of(existing));

		DeviceRegisterResponse response = service.registerDevice("  device-3  ");

		assertThat(response.userId()).isEqualTo(existingUserId);
		assertThat(response.deviceId()).isEqualTo("device-3");
	}
}
