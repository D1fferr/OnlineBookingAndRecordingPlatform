CREATE TABLE providers
(
    id           uuid PRIMARY KEY,
    name         VARCHAR(255)        NOT NULL,
    email        VARCHAR(255) UNIQUE NOT NULL,
    service_type VARCHAR(255)        NOT NULL,
    timezone     VARCHAR(255)        NOT NULL,
    avatar_url   VARCHAR,
    is_blocked   BOOLEAN DEFAULT FALSE,
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
    slot_step INT NOT NULL DEFAULT 30,
    is_active BOOLEAN DEFAULT FALSE,
    unique (provider_id, day_of_week)
);
CREATE TABLE appointments
(
    id           uuid PRIMARY KEY,
    provider_id uuid references providers(id) ON DELETE CASCADE NOT NULL,
    start_time TIMESTAMPTZ,
    end_time TIMESTAMPTZ,
    client_name VARCHAR NOT NULL,
    client_email VARCHAR NOT NULL,
    client_comment VARCHAR,
    status VARCHAR DEFAULT 'PENDING',
    is_reminder_sent BOOLEAN DEFAULT FALSE,
    secure_token uuid NOT NULL,
    created_at   TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE (provider_id, start_time)
);
CREATE TABLE services
(
    id           uuid PRIMARY KEY,
    provider_id uuid references providers(id) ON DELETE CASCADE NOT NULL,
    service_name VARCHAR NOT NULL,
    duration INT NOT NULL,
    price INT NOT NULL,
    description VARCHAR NOT NULL ,
    created_at   TIMESTAMPTZ DEFAULT NOW(),
    updated_at   TIMESTAMPTZ DEFAULT NOW()

);