package com.nurapp.user.auth;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nurapp.user.domain.AuthIdentity;
import com.nurapp.user.domain.AuthIdentityRepository;
import com.nurapp.user.domain.User;
import com.nurapp.user.domain.UserRepository;

@Service
public class DeviceAuthService {

	private final UserRepository users;
	private final AuthIdentityRepository identities;
	private final JwtService jwt;
	private final RefreshTokenService refreshTokens;

	public DeviceAuthService(UserRepository users, AuthIdentityRepository identities, JwtService jwt,
			RefreshTokenService refreshTokens) {
		this.users = users;
		this.identities = identities;
		this.jwt = jwt;
		this.refreshTokens = refreshTokens;
	}

	@Transactional
	public DeviceRegisterResponse registerDevice(String deviceId) {
		String did = (deviceId == null || deviceId.isBlank()) ? UUID.randomUUID().toString() : deviceId.trim();

		var existing = identities.findByProviderAndProviderUid("device", did);
		UUID userId;
		boolean created;
		if (existing.isPresent()) {
			userId = existing.get().getUserId();
			created = false;
		} else {
			User user = users.save(new User());
			identities.save(AuthIdentity.device(user.getId(), did));
			userId = user.getId();
			created = true;
		}

		return new DeviceRegisterResponse(userId, did, created, jwt.issueAccessToken(userId),
				refreshTokens.issue(userId));
	}
}