package com.hotel.hotel_loyalty.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Payment method data object, maps to table "payment_method".
 * <p>
 * Seed data (UC3 / PaymentTypeEnum):
 * <ul>
 *   <li>PM-1 — Points Only (Type 1)</li>
 *   <li>PM-2 — Points + Cash (Type 2)</li>
 *   <li>PM-3 — Points + Voucher (Type 3)</li>
 * </ul>
 *
 * @author wukewei
 * @since 1.0
 */
@Data
@EqualsAndHashCode(of = "id")
public class PaymentMethodDO {

    /** Auto-increment primary key. */
    private Long id;

    /** Business key — unique payment method identifier. */
    private String pmid;

    /** Display name, e.g. "Points Only", "Points + Cash". */
    private String name;

    /** Description of how this method works. */
    private String description;

    /** Row creation time (Alibaba rule). */
    private LocalDateTime gmtCreate;

    /** Row last modification time (Alibaba rule). */
    private LocalDateTime gmtModified;
}