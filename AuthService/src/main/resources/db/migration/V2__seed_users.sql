
INSERT INTO users (id, email, password_hash, role, avatar_url, is_blocked, block_reason, created_at)
VALUES
    (
        'a1b2c3d4-e5f6-4a5b-8c9d-0123456789ab',
        'admin@service.com',
        '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36',
        'ROLE_ADMIN',
        null,
        false,
        null,
        NOW() - INTERVAL '30 days'
    ),
    (
        'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc',
        'john.barber@example.com',
        '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36',
        'ROLE_PROVIDER',
        null,
        false,
        null,
        NOW() - INTERVAL '20 days'
    ),
    (
        'c3d4e5f6-a7b8-4c5d-0e1f-23456789abcd',
        'elena.massage@example.com',
        '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36',
        'ROLE_PROVIDER',
        null,
        true,
        'Multiple customer complaints regarding schedule cancellation',
        NOW() - INTERVAL '15 days'
    ),
    (
        'd4e5f6a7-b8c9-4d0e-1f2a-3456789abcde',
        'alex.smith@example.com',
        '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36',
        'ROLE_CLIENT',
        null,
        false,
        null,
        NOW() - INTERVAL '10 days'
    ),
    (
        'e5f6a7b8-c9d0-4e1f-2a3b-456789abcdef',
        'maria.garcia@example.com',
        '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36',
        'ROLE_CLIENT',
        null,
        false,
        null,
        NOW() - INTERVAL '5 days'
    )
ON CONFLICT (id) DO NOTHING;