INSERT INTO providers (id, name, email, service_type, timezone, avatar_url, is_blocked, created_at)
VALUES (
           'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc',
           'John Doe',
           'john.barber@example.com',
           'Barber / Hairdresser',
           'Europe/Kyiv',
           NULL,
           FALSE,
           NOW() - INTERVAL '20 days'
       ) ON CONFLICT (id) DO NOTHING;

INSERT INTO providers (id, name, email, service_type, timezone, avatar_url, is_blocked, created_at)
VALUES (
           'c3d4e5f6-a7b8-4c5d-0e1f-23456789abcd',
           'Elena Smith',
           'elena.massage@example.com',
           'Massage Therapist',
           'Europe/Kyiv',
           NULL,
           TRUE,
           NOW() - INTERVAL '15 days'
       ) ON CONFLICT (id) DO NOTHING;

INSERT INTO providers (id, name, email, service_type, timezone, avatar_url, is_blocked, created_at)
VALUES (
           'f6a7b8c9-d0e1-4f2a-3b4c-56789abcdef0',
           'Alex Rivera',
           'alex.cosmetology@example.com',
           'Cosmetologist',
           'Europe/Kyiv',
           NULL,
           FALSE,
           NOW() - INTERVAL '8 days'
       ) ON CONFLICT (id) DO NOTHING;

INSERT INTO working_hours (id, provider_id, day_of_week, start_time, end_time, break_start_time, break_end_time, slot_step, is_active)
VALUES
    ('a0000001-0000-0000-0000-000000000001', 'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc', 1, '09:00:00', '18:00:00', '13:00:00', '14:00:00', 30, TRUE),  -- Пн
    ('a0000001-0000-0000-0000-000000000002', 'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc', 2, '09:00:00', '18:00:00', '13:00:00', '14:00:00', 30, TRUE),  -- Вт
    ('a0000001-0000-0000-0000-000000000003', 'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc', 3, '10:00:00', '19:00:00', '14:00:00', '15:00:00', 30, TRUE),  -- Ср (зсунутий графік)
    ('a0000001-0000-0000-0000-000000000004', 'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc', 4, '09:00:00', '18:00:00', '13:00:00', '14:00:00', 30, TRUE),  -- Чт
    ('a0000001-0000-0000-0000-000000000005', 'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc', 5, '09:00:00', '17:00:00', '12:30:00', '13:30:00', 30, TRUE),  -- Пт (скорочений день)
    ('a0000001-0000-0000-0000-000000000006', 'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc', 6, NULL, NULL, NULL, NULL, 30, FALSE),                        -- Сб (Вихідний)
    ('a0000001-0000-0000-0000-000000000007', 'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc', 7, NULL, NULL, NULL, NULL, 30, FALSE);                        -- Нд (Вихідний)

INSERT INTO working_hours (id, provider_id, day_of_week, start_time, end_time, break_start_time, break_end_time, slot_step, is_active)
VALUES
    ('a0000002-0000-0000-0000-000000000001', 'c3d4e5f6-a7b8-4c5d-0e1f-23456789abcd', 1, NULL, NULL, NULL, NULL, 45, FALSE),                        -- Пн (Вихідний)
    ('a0000002-0000-0000-0000-000000000002', 'c3d4e5f6-a7b8-4c5d-0e1f-23456789abcd', 2, NULL, NULL, NULL, NULL, 45, FALSE),                        -- Вт (Вихідний)
    ('a0000002-0000-0000-0000-000000000003', 'c3d4e5f6-a7b8-4c5d-0e1f-23456789abcd', 3, '11:00:00', '20:00:00', '15:00:00', '16:00:00', 45, TRUE),  -- Ср
    ('a0000002-0000-0000-0000-000000000004', 'c3d4e5f6-a7b8-4c5d-0e1f-23456789abcd', 4, '11:00:00', '20:00:00', '15:00:00', '16:00:00', 45, TRUE),  -- Чт
    ('a0000002-0000-0000-0000-000000000005', 'c3d4e5f6-a7b8-4c5d-0e1f-23456789abcd', 5, '10:00:00', '19:00:00', '14:00:00', '15:00:00', 45, TRUE),  -- Пт
    ('a0000002-0000-0000-0000-000000000006', 'c3d4e5f6-a7b8-4c5d-0e1f-23456789abcd', 6, '10:00:00', '16:00:00', '13:00:00', '13:30:00', 45, TRUE),  -- Сб
    ('a0000002-0000-0000-0000-000000000007', 'c3d4e5f6-a7b8-4c5d-0e1f-23456789abcd', 7, '10:00:00', '15:00:00', NULL, NULL, 45, TRUE);              -- Нд

INSERT INTO working_hours (id, provider_id, day_of_week, start_time, end_time, break_start_time, break_end_time, slot_step, is_active)
VALUES
    ('a0000003-0000-0000-0000-000000000001', 'f6a7b8c9-d0e1-4f2a-3b4c-56789abcdef0', 1, '08:30:00', '16:30:00', '12:00:00', '13:00:00', 30, TRUE),  -- Пн
    ('a0000003-0000-0000-0000-000000000002', 'f6a7b8c9-d0e1-4f2a-3b4c-56789abcdef0', 2, '08:30:00', '16:30:00', '12:00:00', '13:00:00', 30, TRUE),  -- Вт
    ('a0000003-0000-0000-0000-000000000003', 'f6a7b8c9-d0e1-4f2a-3b4c-56789abcdef0', 3, NULL, NULL, NULL, NULL, 30, FALSE),                        -- Ср (Вихідний)
    ('a0000003-0000-0000-0000-000000000004', 'f6a7b8c9-d0e1-4f2a-3b4c-56789abcdef0', 4, NULL, NULL, NULL, NULL, 30, FALSE),                        -- Чт (Вихідний)
    ('a0000003-0000-0000-0000-000000000005', 'f6a7b8c9-d0e1-4f2a-3b4c-56789abcdef0', 5, '09:00:00', '18:00:00', '13:00:00', '14:00:00', 30, TRUE),  -- Пт
    ('a0000003-0000-0000-0000-000000000006', 'f6a7b8c9-d0e1-4f2a-3b4c-56789abcdef0', 6, '09:00:00', '17:00:00', '13:00:00', '13:30:00', 30, TRUE),  -- Сб
    ('a0000003-0000-0000-0000-000000000007', 'f6a7b8c9-d0e1-4f2a-3b4c-56789abcdef0', 7, '10:00:00', '15:00:00', NULL, NULL, 30, TRUE);              -- Нд


INSERT INTO services (id, provider_id, service_name, duration, price, description, created_at, updated_at)
VALUES
    (
        'b0000001-0000-0000-0000-000000000001',
        'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc',
        'Classic Men''s Haircut',
        45,
        25,
        'Traditional hair trimming, washing, and professional styling.',
        NOW(), NOW()
    ),
    (
        'b0000001-0000-0000-0000-000000000002',
        'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc',
        'Beard Trim & Modeling',
        30,
        15,
        'Precision beard shaping, razor line up, and nourishing oil application.',
        NOW(), NOW()
    ),
    (
        'b0000001-0000-0000-0000-000000000003',
        'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc',
        'Full Combo (Haircut + Beard)',
        75,
        35,
        'Complete grooming package including haircut, beard styling, and hot towel relaxation.',
        NOW(), NOW()
    );

INSERT INTO services (id, provider_id, service_name, duration, price, description, created_at, updated_at)
VALUES
    (
        'b0000002-0000-0000-0000-000000000001',
        'c3d4e5f6-a7b8-4c5d-0e1f-23456789abcd',
        'Deep Tissue Massage',
        60,
        50,
        'Targeted muscle therapy focusing on releasing chronic tension and pain.',
        NOW(), NOW()
    ),
    (
        'b0000002-0000-0000-0000-000000000002',
        'c3d4e5f6-a7b8-4c5d-0e1f-23456789abcd',
        'Relaxing Aromatherapy Massage',
        90,
        70,
        'Gentle full-body relaxation massage using essential aromatic oils.',
        NOW(), NOW()
    );

INSERT INTO services (id, provider_id, service_name, duration, price, description, created_at, updated_at)
VALUES
    (
        'b0000003-0000-0000-0000-000000000001',
        'f6a7b8c9-d0e1-4f2a-3b4c-56789abcdef0',
        'Deep Facial Cleaning',
        60,
        40,
        'Comprehensive skin exfoliation, pore cleansing, and moisturizing mask.',
        NOW(), NOW()
    ),
    (
        'b0000003-0000-0000-0000-000000000002',
        'f6a7b8c9-d0e1-4f2a-3b4c-56789abcdef0',
        'Anti-Aging Facial Therapy',
        45,
        60,
        'Rejuvenating serum application with micro-current facial lifting.',
        NOW(), NOW()
    );


INSERT INTO appointments (id, provider_id, start_time, end_time, client_name, client_email, client_comment, status, is_reminder_sent, secure_token, created_at)
VALUES (
           'c0000001-0000-0000-0000-000000000001',
           'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc',
           NOW() + INTERVAL '1 day' + INTERVAL '9 hours 30 minutes',
           NOW() + INTERVAL '1 day' + INTERVAL '10 hours 15 minutes',
           'Alex Smith',
           'alex.smith@example.com',
           'First time visiting, classic fade preferred.',
           'CONFIRMED',
           FALSE,
           '11112222-3333-4444-5555-666677778888',
           NOW()
       );

INSERT INTO appointments (id, provider_id, start_time, end_time, client_name, client_email, client_comment, status, is_reminder_sent, secure_token, created_at)
VALUES (
           'c0000001-0000-0000-0000-000000000002',
           'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc',
           NOW() + INTERVAL '1 day' + INTERVAL '11 hours',
           NOW() + INTERVAL '1 day' + INTERVAL '11 hours 30 minutes',
           'Maria Garcia',
           'maria.garcia@example.com',
           'Booking beard trim for my husband.',
           'CONFIRMED',
           FALSE,
           '22223333-4444-5555-6666-777788889999',
           NOW()
       );

INSERT INTO appointments (id, provider_id, start_time, end_time, client_name, client_email, client_comment, status, is_reminder_sent, secure_token, created_at)
VALUES (
           'c0000001-0000-0000-0000-000000000003',
           'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc',
           NOW() + INTERVAL '1 day' + INTERVAL '15 hours',
           NOW() + INTERVAL '1 day' + INTERVAL '15 hours 45 minutes',
           'Michael Jordan',
           'mj@example.com',
           'Regular trim.',
           'PENDING',
           FALSE,
           '33334444-5555-6666-7777-888899990000',
           NOW()
       );

INSERT INTO appointments (id, provider_id, start_time, end_time, client_name, client_email, client_comment, status, is_reminder_sent, secure_token, created_at)
VALUES (
           'c0000001-0000-0000-0000-000000000004',
           'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc',
           NOW() + INTERVAL '2 days' + INTERVAL '10 hours',
           NOW() + INTERVAL '2 days' + INTERVAL '11 hours 15 minutes',
           'David Beckham',
           'david.b@example.com',
           'Full Combo session.',
           'CONFIRMED',
           FALSE,
           '44445555-6666-7777-8888-999900001111',
           NOW()
       );

INSERT INTO appointments (id, provider_id, start_time, end_time, client_name, client_email, client_comment, status, is_reminder_sent, secure_token, created_at)
VALUES (
           'c0000001-0000-0000-0000-000000000005',
           'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc',
           NOW() + INTERVAL '3 days' + INTERVAL '14 hours',
           NOW() + INTERVAL '3 days' + INTERVAL '14 hours 45 minutes',
           'Robert Downey',
           'rdj@example.com',
           'Please be on time.',
           'CONFIRMED',
           FALSE,
           '55556666-7777-8888-9999-000011112222',
           NOW()
       );

INSERT INTO appointments (id, provider_id, start_time, end_time, client_name, client_email, client_comment, status, is_reminder_sent, secure_token, created_at)
VALUES (
           'c0000001-0000-0000-0000-000000000006',
           'b2c3d4e5-f6a7-4b5c-9d0e-123456789abc',
           NOW() + INTERVAL '4 days' + INTERVAL '16 hours',
           NOW() + INTERVAL '4 days' + INTERVAL '16 hours 30 minutes',
           'Chris Evans',
           'captain@example.com',
           'Beard line up only.',
           'CONFIRMED',
           FALSE,
           '66667777-8888-9999-0000-111122223333',
           NOW()
       );

INSERT INTO appointments (id, provider_id, start_time, end_time, client_name, client_email, client_comment, status, is_reminder_sent, secure_token, created_at)
VALUES (
           'c0000002-0000-0000-0000-000000000001',
           'c3d4e5f6-a7b8-4c5d-0e1f-23456789abcd',
           NOW() + INTERVAL '2 days' + INTERVAL '12 hours',
           NOW() + INTERVAL '2 days' + INTERVAL '13 hours',
           'Anna Watson',
           'anna.w@example.com',
           'Back pain focus.',
           'CONFIRMED',
           FALSE,
           '77778888-9999-0000-1111-222233334444',
           NOW()
       );

INSERT INTO appointments (id, provider_id, start_time, end_time, client_name, client_email, client_comment, status, is_reminder_sent, secure_token, created_at)
VALUES (
           'c0000002-0000-0000-0000-000000000002',
           'c3d4e5f6-a7b8-4c5d-0e1f-23456789abcd',
           NOW() + INTERVAL '2 days' + INTERVAL '16 hours 30 minutes',
           NOW() + INTERVAL '2 days' + INTERVAL '18 hours',
           'Sophia Loren',
           'sophia@example.com',
           'Aromatherapy massage.',
           'CONFIRMED',
           FALSE,
           '88889999-0000-1111-2222-333344445555',
           NOW()
       );

INSERT INTO appointments (id, provider_id, start_time, end_time, client_name, client_email, client_comment, status, is_reminder_sent, secure_token, created_at)
VALUES (
           'c0000002-0000-0000-0000-000000000003',
           'c3d4e5f6-a7b8-4c5d-0e1f-23456789abcd',
           NOW() + INTERVAL '3 days' + INTERVAL '11 hours 30 minutes',
           NOW() + INTERVAL '3 days' + INTERVAL '12 hours 30 minutes',
           'Emma Stone',
           'emma.s@example.com',
           'General relaxation.',
           'PENDING',
           FALSE,
           '99990000-1111-2222-3333-444455556666',
           NOW()
       );

INSERT INTO appointments (id, provider_id, start_time, end_time, client_name, client_email, client_comment, status, is_reminder_sent, secure_token, created_at)
VALUES (
           'c0000002-0000-0000-0000-000000000004',
           'c3d4e5f6-a7b8-4c5d-0e1f-23456789abcd',
           NOW() + INTERVAL '5 days' + INTERVAL '14 hours',
           NOW() + INTERVAL '5 days' + INTERVAL '15 hours 30 minutes',
           'Scarlett Johansson',
           'scarlett@example.com',
           'Gift certificate usage.',
           'CONFIRMED',
           FALSE,
           '00001111-2222-3333-4444-555566667777',
           NOW()
       );