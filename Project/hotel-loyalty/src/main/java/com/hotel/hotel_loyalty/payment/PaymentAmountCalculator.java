package com.hotel.hotel_loyalty.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculates the cash or voucher amount for the points shortfall.
 *
 * @author Fordring
 */
public final class PaymentAmountCalculator {

    private static final BigDecimal MONEY_PER_POINT = new BigDecimal("0.1");

    private PaymentAmountCalculator() {
    }

    public static BigDecimal calculateSupplement(PaymentRequest request) {
        return BigDecimal.valueOf(request.getProductTotalPoints() - request.getPointsUsed())
                .multiply(MONEY_PER_POINT)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
