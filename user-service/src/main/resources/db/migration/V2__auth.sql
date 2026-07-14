-- Kullanıcılar
CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    status      VARCHAR(20)  NOT NULL DEFAULT 'active',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Kimlik yöntemleri: device | email | google | apple
CREATE TABLE auth_identities (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider      VARCHAR(20)  NOT NULL,
    provider_uid  VARCHAR(255) NOT NULL,
    email         VARCHAR(255),
    password_hash VARCHAR(255),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (provider, provider_uid)
);

CREATE INDEX idx_auth_identities_user_id ON auth_identities (user_id);