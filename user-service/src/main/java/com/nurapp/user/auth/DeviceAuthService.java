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

	public DeviceAuthService(UserRepository users, AuthIdentityRepository identities, JwtService jwt) {
		this.users = users;
		this.identities = identities;
		this.jwt = jwt;
	}

	@Transactional
	public DeviceRegisterResponse registerDevice(String deviceId) {
		String did = (deviceId == null || deviceId.isBlank()) ? UUID.randomUUID().toString() : deviceId.trim();

		var existing = identities.findByProviderAndProviderUid("device", did);
		if (existing.isPresent()) {
			UUID uid = existing.get().getUserId();
			return new DeviceRegisterResponse(uid, did, false, jwt.issueAccessToken(uid));
		}

		User user = users.save(new User());
		identities.save(AuthIdentity.device(user.getId(), did));
		return new DeviceRegisterResponse(user.getId(), did, true, jwt.issueAccessToken(user.getId()));
	}
}