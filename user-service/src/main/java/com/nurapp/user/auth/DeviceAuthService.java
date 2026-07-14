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

	public DeviceAuthService(UserRepository users, AuthIdentityRepository identities) {
		this.users = users;
		this.identities = identities;
	}

	@Transactional
	public DeviceRegisterResponse registerDevice(String deviceId) {
		String did = (deviceId == null || deviceId.isBlank()) ? UUID.randomUUID().toString() : deviceId.trim();

		// Aynı cihaz daha önce kaydolduysa mevcut kullanıcıyı döndür (idempotent)
		var existing = identities.findByProviderAndProviderUid("device", did);
		if (existing.isPresent()) {
			return new DeviceRegisterResponse(existing.get().getUserId(), did, false);
		}

		User user = users.save(new User());
		identities.save(AuthIdentity.device(user.getId(), did));
		return new DeviceRegisterResponse(user.getId(), did, true);
	}
}