CREATE TABLE providers
(
    id           uuid PRIMARY KEY,
    name         VARCHAR(255)        NOT NULL,
    email        VARCHAR(255) UNIQUE NOT NULL,
    service_type VARCHAR(255)        NOT NULL,
    timezone     VARCHAR(255)        NOT NULL,
    avatar_url   VARCHAR,
    created_at   TIMESTAMPTZ DEFAULT NOW()
);
CREATE TABLE working_hours
(
    id           uuid PRIMARY KEY,
    provider_id uuid references providers(id) ON DELETE CASCADE NOT NULL,
    day_of_week INT NOT NULL,
    start_time TIME,
    end_time TIME,
    break_start_time TIME,
    break_end_time TIME,
    is_active BOOLEAN DEFAULT FALSE,
    unique (provider_id, day_of_week)
);