package com.nurapp.user.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final DeviceAuthService deviceAuthService;

	public AuthController(DeviceAuthService deviceAuthService) {
		this.deviceAuthService = deviceAuthService;
	}

	@PostMapping("/device")
	public DeviceRegisterResponse device(@RequestBody(required = false) DeviceRegisterRequest req) {
		String deviceId = (req == null) ? null : req.deviceId();
		return deviceAuthService.registerDevice(deviceId);
	}
}