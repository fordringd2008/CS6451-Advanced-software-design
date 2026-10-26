package com.hotel.hotel_loyalty.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Immutable input for a payment strategy — carries everything a strategy
 * needs to validate and calculate a redemption payment (UC3).
 *
 * @author wukewei
 * @since 1.0
 */
@Getter
@ToString
@RequiredArgsConstructor
public class PaymentRequest {

    /** User placing the order. */
    private final String uid;
    /** Product being redeemed. */
    private final String pid;
    /** Payment method chosen (PM-1 / PM-2 / PM-3). */
    private final String pmid;
    /** Points the user wants to spend. */
    private final Integer pointsUsed;
    /** Cash supplement (Type 2 only). */
    private final BigDecimal cashUsed;
    /** Voucher supplement (Type 3 only). */
    private final BigDecimal voucherUsed;
    /** Product full points price (for validation). */
    private final Integer productTotalPoints;
    /** Product minimum points required (for validation). */
    private final Integer productMinPoints;
    /** User's current point balance (for validation). */
    private final Integer userCurrentPoint;
}