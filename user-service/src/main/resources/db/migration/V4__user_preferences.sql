CREATE TABLE user_preferences (
    user_id               UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    language              VARCHAR(8)  NOT NULL DEFAULT 'en',
    calculation_method    VARCHAR(32) NOT NULL DEFAULT 'MWL',
    madhab                VARCHAR(16) NOT NULL DEFAULT 'shafi',
    theme                 VARCHAR(16) NOT NULL DEFAULT 'system',
    notifications_enabled BOOLEAN     NOT NULL DEFAULT true,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT now()
);
