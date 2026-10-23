-- ============================================================
-- H2 seed data for hotel-loyalty (production-necessary only)
-- Test/mock data lives in src/test/resources/data.sql which
-- shadows this file on the test classpath.
-- ============================================================

-- Payment methods (UC3 / PaymentTypeEnum Type 1/2/3)
INSERT INTO payment_method (id, pmid, name, description, gmt_create, gmt_modified) VALUES
(1, 'PM-1', 'Points Only',       'Pay entirely with points', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(2, 'PM-2', 'Points + Cash',     'Pay with points plus a cash supplement', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(3, 'PM-3', 'Points + Voucher',  'Pay with points plus a voucher supplement', '2026-01-01 00:00:00', '2026-01-01 00:00:00');

-- Points rules: level multipliers L1..L5 (BY_LEVEL, threshold = NULL = no min order amount;
-- points_ratio = percentage per candidate_objects: order amount x ratio% = points)
INSERT INTO points_rule (prid, name, type, threshold, points_ratio, start_time, end_time, description, gmt_create, gmt_modified) VALUES
('PR-L1', 'Level 1 multiplier', 1, NULL, 100, NULL, NULL, 'Base earn rate for L1 members: order amount x 100%', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
('PR-L2', 'Level 2 multiplier', 1, NULL, 120, NULL, NULL, 'Earn rate for L2 members: order amount x 120%', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
('PR-L3', 'Level 3 multiplier', 1, NULL, 150, NULL, NULL, 'Earn rate for L3 members: order amount x 150%', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
('PR-L4', 'Level 4 multiplier', 1, NULL, 180, NULL, NULL, 'Earn rate for L4 members: order amount x 180%', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
('PR-L5', 'Level 5 multiplier', 1, NULL, 200, NULL, NULL, 'Earn rate for L5 members: order amount x 200%', '2026-01-01 00:00:00', '2026-01-01 00:00:00');

-- Seed users: one manager + one guest, BCrypt-hashed passwords (cost 10)
-- Demo credentials: admin / admin123 (manager), guest / guest123 (guest)
INSERT INTO "user" (id, uid, name, password, status, role, register_time, level, current_point, gmt_create, gmt_modified) VALUES
(1, 'admin', 'admin', '$2a$10$Ia0RNpJpsE7y.VGIGtgLdOoHLyJvlk6tzQbvqpiYqwa/JsCPMUo6q', 0, 1, '2026-01-01 00:00:00', 5, 0, '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(2, 'guest', 'alice', '$2a$10$Cpv6yorUPaOqxXZgUdmfweDJfBx4dvnAC59Mu1wWrx4kLBX4UtjDq', 0, 0, '2026-01-02 00:00:00', 2, 5000, '2026-01-02 00:00:00', '2026-01-02 00:00:00');