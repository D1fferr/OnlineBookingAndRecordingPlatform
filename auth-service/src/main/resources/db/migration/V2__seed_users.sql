
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
    )
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, email, password_hash, role, avatar_url, is_blocked, block_reason, created_at)
VALUES
    ('11111111-1111-4111-8111-111111111111', 'trump@example.com',     '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36', 'ROLE_PROVIDER', '/api/images/11111111-1111-4111-8111-111111111111.jpg', FALSE, NULL, NOW()),
    ('22222222-2222-4222-8222-222222222222', 'edward@example.com', '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36', 'ROLE_PROVIDER', '/api/images/22222222-2222-4222-8222-222222222222.jpg', FALSE, NULL, NOW()),
    ('33333333-3333-4333-8333-333333333333', 'mary@example.com', '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36', 'ROLE_PROVIDER', '/api/images/33333333-3333-4333-8333-333333333333.jpg', FALSE, NULL, NOW()),
    ('44444444-4444-4444-8444-444444444444', 'quentin@example.com', '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36', 'ROLE_PROVIDER', '/api/images/44444444-4444-4444-8444-444444444444.jpg', FALSE, NULL, NOW()),
    ('55555555-5555-4555-8555-555555555555', 'dominic@example.com',     '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36', 'ROLE_PROVIDER', '/api/images/55555555-5555-4555-8555-555555555555.jpg', FALSE, NULL, NOW()),
    ('66666666-6666-4666-8666-666666666666', 'heisenberg@example.com',     '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36', 'ROLE_PROVIDER', '/api/images/66666666-6666-4666-8666-666666666666.jpg', FALSE, NULL, NOW())
ON CONFLICT (id) DO NOTHING;