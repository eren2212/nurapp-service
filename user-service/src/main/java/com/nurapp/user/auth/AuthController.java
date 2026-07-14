package com.nurapp.user.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final DeviceAuthService deviceAuthService;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;

	public AuthController(DeviceAuthService deviceAuthService, JwtService jwtService,
			RefreshTokenService refreshTokenService) {
		this.deviceAuthService = deviceAuthService;
		this.jwtService = jwtService;
		this.refreshTokenService = refreshTokenService;
	}

	@PostMapping("/device")
	public DeviceRegisterResponse device(@RequestBody(required = false) DeviceRegisterRequest req) {
		String deviceId = (req == null) ? null : req.deviceId();
		return deviceAuthService.registerDevice(deviceId);
	}

	@PostMapping("/token/refresh")
	public TokenPairResponse refresh(@RequestBody TokenRefreshRequest req) {
		var userId = refreshTokenService.consume(req.refreshToken()); // eskiyi iptal et
		return new TokenPairResponse(jwtService.issueAccessToken(userId), refreshTokenService.issue(userId)); // yenisini
																												// ver
	}
}