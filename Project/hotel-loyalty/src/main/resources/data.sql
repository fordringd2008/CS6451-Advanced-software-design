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

-- Points rules: level multipliers L1..L5 (BY_LEVEL, threshold = level number)
INSERT INTO points_rule (id, prid, name, type, threshold, points_ratio, gmt_create, gmt_modified) VALUES
(1, 'PR-L1', 'Level 1 multiplier', 1, 1, 1.0, '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(2, 'PR-L2', 'Level 2 multiplier', 1, 2, 1.2, '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(3, 'PR-L3', 'Level 3 multiplier', 1, 3, 1.5, '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(4, 'PR-L4', 'Level 4 multiplier', 1, 4, 1.8, '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(5, 'PR-L5', 'Level 5 multiplier', 1, 5, 2.0, '2026-01-01 00:00:00', '2026-01-01 00:00:00');