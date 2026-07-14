package com.nurapp.user.auth;

import java.util.UUID;

public record DeviceRegisterResponse(UUID userId, String deviceId, boolean created, String accessToken) {
}