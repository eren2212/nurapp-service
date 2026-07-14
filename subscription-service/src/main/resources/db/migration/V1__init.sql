-- RevenueCat webhook olayları (idempotency için)
CREATE TABLE webhook_events (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id    VARCHAR(255) NOT NULL UNIQUE,
    type        VARCHAR(64),
    received_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Kullanıcı hak sahiplikleri (premium vb.)
CREATE TABLE entitlements (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL,
    entitlement VARCHAR(64) NOT NULL,
    active      BOOLEAN     NOT NULL DEFAULT false,
    expires_at  TIMESTAMPTZ,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (user_id, entitlement)
);

CREATE INDEX idx_entitlements_user_id ON entitlements (user_id);
