package com.hotel.hotel_loyalty.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Redemption order data object, maps to table "order" (one order = one product).
 * <p>
 * Design decisions (consistency audit):
 * <ul>
 *   <li>Includes "pmid" (payment method FK) — required by UC3 sequence diagram
 *       (createOrder takes PMID) but missing in candidate_objects.</li>
 *   <li>Excludes "ptid" (points transaction FK) — avoids the circular FK
 *       Order↔PointsTransaction found in the ER diagram; points flow is
 *       navigated from PointsTransaction.relatedOrderId instead.</li>
 * </ul>
 *
 * @author wukewei
 * @since 1.0
 */
@Data
@EqualsAndHashCode(of = "id")
public class OrderDO {

    /** Auto-increment primary key. */
    private Long id;

    /** Business key — unique order identifier. */
    private String oid;

    /** FK — user who placed the order (user.uid). */
    private String uid;

    /** FK — the single product in this order (product.pid). */
    private String pid;

    /** FK — payment method used (payment_method.pmid). */
    private String pmid;

    /** Points consumed in this order (always >= 0). */
    private Integer pointsUsed;

    /** Cash portion in combination payment (Type 2), null for pure points. */
    private BigDecimal cashUsed;

    /** Voucher portion in combination payment (Type 3), null for pure points. */
    private BigDecimal voucherUsed;

    /** Order status code — see {@link com.hotel.hotel_loyalty.enums.OrderStatusEnum}. */
    private Integer status;

    /** Row creation time (Alibaba rule). */
    private LocalDateTime gmtCreate;

    /** Row last modification time (Alibaba rule). */
    private LocalDateTime gmtModified;
}