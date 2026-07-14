CREATE TABLE service_meta (
    id           SMALLINT PRIMARY KEY,
    service_name VARCHAR(64) NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO service_meta (id, service_name) VALUES (1, 'user-service');