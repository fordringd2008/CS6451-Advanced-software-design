package com.hotel.hotel_loyalty.vo;

import lombok.Data;

import java.util.List;

/**
 * Confirm-payment page payload (GUI: product + payment info + method radios).
 *
 * @author Fordring
 * @since 1.0
 */
@Data
public class ConfirmPaymentVO {

    /** Product being redeemed. */
    private String pid;

    /** Product display name. */
    private String name;

    /** Product image URL. */
    private String imageUrl;

    /** Full points price (GUI: "Total Points Required: 800 pts"). */
    private Integer totalPointsRequired;

    /** Minimum points (GUI: "Min Points Required: 400 pts"). */
    private Integer minPointsRequired;

    /** Guest's available balance (GUI: "Available Balance: 2,450 pts"). */
    private Integer availableBalance;

    /** All selectable payment methods (GUI: Type 1/2/3 radios). */
    private List<PaymentMethodVO> paymentMethods;
}