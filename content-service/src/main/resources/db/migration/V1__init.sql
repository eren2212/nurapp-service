CREATE TABLE dhikr_programs (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key            VARCHAR(64) NOT NULL UNIQUE,
    default_target INTEGER     NOT NULL,
    premium        BOOLEAN     NOT NULL DEFAULT false,
    sort_order     INTEGER     NOT NULL
);

CREATE TABLE dhikr_program_translations (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    program_id  UUID NOT NULL REFERENCES dhikr_programs(id) ON DELETE CASCADE,
    locale      VARCHAR(8)   NOT NULL,
    name        VARCHAR(128) NOT NULL,
    description VARCHAR(512),
    UNIQUE (program_id, locale)
);

CREATE INDEX idx_dhikr_tr_locale ON dhikr_program_translations (locale);
