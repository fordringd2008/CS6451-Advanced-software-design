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

-- Reward products (UC5 points mall). Covers all three payment types:
--   * P-001..P-003: points-only (total = min) — affordable for the 5000-point demo guest
--   * P-004:        mid-range, can go points-only or points + cash/voucher
--   * P-005..P-006: premium — total > guest balance, must use points + cash/voucher (PM-2/PM-3)
INSERT INTO product (id, pid, name, image_url, total_points_required, min_points_required, total_stock, remaining_stock, gmt_create, gmt_modified) VALUES
(1, 'P-001', 'Breakfast Buffet Voucher',   'https://images.example.com/breakfast.jpg',  500,  500, 200, 200, '2026-01-03 00:00:00', '2026-01-03 00:00:00'),
(2, 'P-002', 'Afternoon Tea for Two',      'https://images.example.com/afternoon-tea.jpg', 800, 800, 100, 100, '2026-01-03 00:00:00', '2026-01-03 00:00:00'),
(3, 'P-003', 'Executive Lounge Day Pass',  'https://images.example.com/lounge.jpg',  1500, 1500,  50,  50, '2026-01-03 00:00:00', '2026-01-03 00:00:00'),
(4, 'P-004', 'Spa Day Pass',               'https://images.example.com/spa.jpg',      3000, 1000,  50,  50, '2026-01-03 00:00:00', '2026-01-03 00:00:00'),
(5, 'P-005', 'Room Upgrade Voucher',       'https://images.example.com/room-upgrade.jpg', 8000, 3000, 30, 30, '2026-01-03 00:00:00', '2026-01-03 00:00:00'),
(6, 'P-006', 'Weekend Stay Package',       'https://images.example.com/weekend-stay.jpg', 15000, 5000, 10, 10, '2026-01-03 00:00:00', '2026-01-03 00:00:00');

-- Explicit seed IDs must advance H2 identity columns before user-created rows.
ALTER TABLE payment_method ALTER COLUMN id RESTART WITH 4;
ALTER TABLE "user" ALTER COLUMN id RESTART WITH 3;
ALTER TABLE product ALTER COLUMN id RESTART WITH 7;