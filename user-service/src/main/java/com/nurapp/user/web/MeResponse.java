package com.nurapp.user.web;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MeResponse(UUID userId, String status, String fullName, OffsetDateTime createdAt) {
}
