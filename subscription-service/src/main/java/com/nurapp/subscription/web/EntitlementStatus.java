package com.nurapp.subscription.web;

import java.time.OffsetDateTime;

public record EntitlementStatus(boolean premium, OffsetDateTime expiresAt) {
}
