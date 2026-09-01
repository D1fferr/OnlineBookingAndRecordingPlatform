INSERT INTO users (id, email, password_hash, role, avatar_url, is_blocked, block_reason, created_at)
VALUES
    ('11111111-1111-4111-8111-111111111111', 'admin@example.com',     '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36', 'ROLE_PROVIDER', '', FALSE, NULL, NOW()),
    ('22222222-2222-4222-8222-222222222222', 'provider1@example.com', '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36', 'ROLE_PROVIDER', '', FALSE, NULL, NOW()),
    ('33333333-3333-4333-8333-333333333333', 'provider2@example.com', '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36', 'ROLE_PROVIDER', '', FALSE, NULL, NOW()),
    ('44444444-4444-4444-8444-444444444444', 'provider3@example.com', '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36', 'ROLE_PROVIDER', '', FALSE, NULL, NOW()),
    ('55555555-5555-4555-8555-555555555555', 'user1@example.com',     '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36', 'ROLE_PROVIDER',     '', FALSE, NULL, NOW()),
    ('66666666-6666-4666-8666-666666666666', 'user2@example.com',     '$2a$10$v6hd6rtGL/xZHTs3C2e4H.jud8a0YBidGOLovGvrTaApqz8qAMv36', 'ROLE_PROVIDER',     '', FALSE, NULL, NOW())
ON CONFLICT (id) DO NOTHING;