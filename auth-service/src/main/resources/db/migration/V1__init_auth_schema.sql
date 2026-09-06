CREATE TABLE users
(
    id            uuid PRIMARY KEY,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    role          VARCHAR(50)         NOT NULL DEFAULT 'ROLE_PROVIDER',
    avatar_url    VARCHAR,
    is_blocked    BOOLEAN                      DEFAULT FALSE,
    block_reason  VARCHAR                      DEFAULT NULL,
    created_at    TIMESTAMPTZ                  DEFAULT NOW()
);