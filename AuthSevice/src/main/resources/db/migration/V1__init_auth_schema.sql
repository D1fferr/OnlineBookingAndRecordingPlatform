CREATE TABLE users (
                       id uuid PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL DEFAULT 'ROLE_PROVIDER',
                       created_at TIMESTAMPTZ DEFAULT NOW()
);