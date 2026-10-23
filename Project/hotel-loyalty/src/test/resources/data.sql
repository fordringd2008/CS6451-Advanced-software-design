-- ============================================================
-- H2 test data for hotel-loyalty (shadows main data.sql on the
-- test classpath). Contains seed data + mock users/products.
-- ============================================================

-- Seed: payment methods (UC3 / PaymentTypeEnum Type 1/2/3)
INSERT INTO payment_method (id, pmid, name, description, gmt_create, gmt_modified) VALUES
(1, 'PM-1', 'Points Only',       'Pay entirely with points', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(2, 'PM-2', 'Points + Cash',     'Pay with points plus a cash supplement', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(3, 'PM-3', 'Points + Voucher',  'Pay with points plus a voucher supplement', '2026-01-01 00:00:00', '2026-01-01 00:00:00');

-- Seed: points rules, level multipliers L1..L5
INSERT INTO points_rule (id, prid, name, type, threshold, points_ratio, gmt_create, gmt_modified) VALUES
(1, 'PR-L1', 'Level 1 multiplier', 1, 1, 1.0, '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(2, 'PR-L2', 'Level 2 multiplier', 1, 2, 1.2, '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(3, 'PR-L3', 'Level 3 multiplier', 1, 3, 1.5, '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(4, 'PR-L4', 'Level 4 multiplier', 1, 4, 1.8, '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(5, 'PR-L5', 'Level 5 multiplier', 1, 5, 2.0, '2026-01-01 00:00:00', '2026-01-01 00:00:00');

-- Mock: one manager, one guest (BCrypt-hashed passwords)
INSERT INTO "user" (id, uid, name, password, status, role, register_time, level, current_point, gmt_create, gmt_modified) VALUES
(1, 'U-ADMIN-01', 'admin', '$2a$10$Ia0RNpJpsE7y.VGIGtgLdOoHLyJvlk6tzQbvqpiYqwa/JsCPMUo6q', 0, 1, '2026-01-01 00:00:00', 5, 0, '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(2, 'U-GUEST-01', 'alice', '$2a$10$Cpv6yorUPaOqxXZgUdmfweDJfBx4dvnAC59Mu1wWrx4kLBX4UtjDq', 0, 0, '2026-01-02 00:00:00', 2, 5000, '2026-01-02 00:00:00', '2026-01-02 00:00:00');

-- Mock: sample products
INSERT INTO product (id, pid, name, image_url, total_points_required, min_points_required, total_stock, remaining_stock, gmt_create, gmt_modified) VALUES
(1, 'P-001', 'Free Night Voucher',    'https://example.com/p001.png', 10000, 5000, 100, 100, '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(2, 'P-002', 'Buffet Dinner for Two', 'https://example.com/p002.png',  3000, 1500,  50,  50, '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(3, 'P-003', 'Late Checkout Pass',    'https://example.com/p003.png',   800,  400, 200, 200, '2026-01-01 00:00:00', '2026-01-01 00:00:00');