package com.hotel.hotel_loyalty.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Points transaction data object, maps to table "points_transaction".
 * <p>
 * Design decisions (consistency audit):
 * <ul>
 *   <li>Added "type" field — missing in candidate_objects but required by the
 *       UC3 sequence diagram (create(UID, -pointsUsed, OID, type=REDEEM)).
 *       Semantics: EARN = positive (check-in / purchase), REDEEM = negative.</li>
 *   <li>Holds "relatedOrderId" (FK to order.oid) — the single direction of the
 *       Order↔PointsTransaction link, avoiding the circular FK in the ER diagram.</li>
 *   <li>"pointsChange" is signed: positive for earning, negative for redemption.</li>
 * </ul>
 *
 * @author wukewei
 * @since 1.0
 */
@Data
@EqualsAndHashCode(of = "id")
public class PointsTransactionDO {

    /** Auto-increment primary key. */
    private Long id;

    /** Business key — unique transaction identifier. */
    private String ptid;

    /** FK — user whose points changed (user.uid). */
    private String uid;

    /** FK — the order that triggered this transaction (order.oid), nullable for non-order earnings. */
    private String relatedOrderId;

    /** Signed points delta: positive = earn, negative = redeem. */
    private Integer pointsChange;

    /** Transaction type: "EARN" or "REDEEM". */
    private String type;

    /** Row creation time (Alibaba rule). */
    private LocalDateTime gmtCreate;

    /** Row last modification time (Alibaba rule). */
    private LocalDateTime gmtModified;
}