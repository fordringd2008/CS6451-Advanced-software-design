-- ============================================================
-- H2 in-memory schema for hotel-loyalty
-- Conventions (Alibaba Java Guidelines):
--   * table names: lowercase, singular
--   * every table has id / gmt_create / gmt_modified
--   * no foreign-key constraints (enforced in application layer)
--   * id is BIGINT AUTO_INCREMENT (mapped to Long in DO)
-- ============================================================

DROP TABLE IF EXISTS "order";
DROP TABLE IF EXISTS points_transaction;
DROP TABLE IF EXISTS payment_method;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS points_rule;
DROP TABLE IF EXISTS "user";

CREATE TABLE "user" (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    uid             VARCHAR(32)  NOT NULL,
    name            VARCHAR(64)  NOT NULL,
    password        VARCHAR(128) NOT NULL,
    status          INT          NOT NULL,          -- 0 normal / 1 inactive / 2 forbidden
    role            INT          NOT NULL,          -- 0 guest / 1 manager
    register_time   TIMESTAMP    NOT NULL,
    level           INT          NOT NULL,          -- 1..5 (L1..L5)
    current_point   INT          NOT NULL DEFAULT 0,
    gmt_create      TIMESTAMP    NOT NULL,
    gmt_modified    TIMESTAMP    NOT NULL,
    CONSTRAINT uk_user_uid UNIQUE (uid)
);

CREATE TABLE product (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    pid                     VARCHAR(32)  NOT NULL,
    name                    VARCHAR(128) NOT NULL,
    image_url               VARCHAR(2048),
    total_points_required   INT          NOT NULL,
    min_points_required     INT          NOT NULL,
    total_stock             INT          NOT NULL,
    remaining_stock         INT          NOT NULL,
    gmt_create              TIMESTAMP    NOT NULL,
    gmt_modified            TIMESTAMP    NOT NULL,
    CONSTRAINT uk_product_pid UNIQUE (pid)
);

CREATE TABLE payment_method (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    pmid            VARCHAR(32)  NOT NULL,
    name            VARCHAR(64)  NOT NULL,
    description     VARCHAR(256),
    gmt_create      TIMESTAMP    NOT NULL,
    gmt_modified    TIMESTAMP    NOT NULL,
    CONSTRAINT uk_payment_method_pmid UNIQUE (pmid)
);

-- "order" is a reserved word in H2, hence the double quotes.
CREATE TABLE "order" (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    oid             VARCHAR(32)  NOT NULL,
    uid             VARCHAR(32)  NOT NULL,          -- FK -> user.uid (app layer)
    pid             VARCHAR(32)  NOT NULL,          -- FK -> product.pid (app layer)
    pmid            VARCHAR(32)  NOT NULL,          -- FK -> payment_method.pmid (app layer)
    points_used     INT          NOT NULL,
    cash_used       DECIMAL(10, 2),
    voucher_used    DECIMAL(10, 2),
    status          INT          NOT NULL,          -- 1 pending / 2 paid / 3 completed / 5 cancelled
    gmt_create      TIMESTAMP    NOT NULL,
    gmt_modified    TIMESTAMP    NOT NULL,
    CONSTRAINT uk_order_oid UNIQUE (oid)
);

CREATE TABLE points_transaction (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    ptid              VARCHAR(32)  NOT NULL,
    uid               VARCHAR(32)  NOT NULL,        -- FK -> user.uid (app layer)
    related_order_id  VARCHAR(32),                  -- FK -> order.oid (app layer, nullable)
    points_change     INT          NOT NULL,        -- signed: + earn / - redeem
    type              VARCHAR(16)  NOT NULL,        -- EARN / REDEEM
    gmt_create        TIMESTAMP    NOT NULL,
    gmt_modified      TIMESTAMP    NOT NULL,
    CONSTRAINT uk_points_transaction_ptid UNIQUE (ptid)
);

CREATE TABLE points_rule (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    prid            VARCHAR(32)  NOT NULL,
    name            VARCHAR(128) NOT NULL,
    type            INT          NOT NULL,          -- 1 by level / 2 by period
    threshold       INT,                            -- min external order amount to apply the rule (candidate_objects)
    points_ratio    INT,                            -- percentage: order amount x ratio% = points (candidate_objects; level multipliers stored as 100/120/150/180/200)
    start_time      TIMESTAMP,                      -- effective period start, type=2 only (candidate_objects)
    end_time        TIMESTAMP,                      -- effective period end, type=2 only (candidate_objects)
    description     VARCHAR(256),                   -- rule description (candidate_objects)
    gmt_create      TIMESTAMP    NOT NULL,
    gmt_modified    TIMESTAMP    NOT NULL,
    CONSTRAINT uk_points_rule_prid UNIQUE (prid)
);