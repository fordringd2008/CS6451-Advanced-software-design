package com.hotel.hotel_loyalty.payment;

import com.hotel.hotel_loyalty.enums.PaymentTypeEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Type 3 — points + voucher combination payment (UC3 Extension 5a).
 * Rules: points must meet the minimum, a positive voucher supplement is required.
 *
 * @author wukewei
 * @since 1.0
 */
@Component
public class PointsPlusVoucherStrategy implements PaymentStrategy {

    @Override
    public PaymentTypeEnum getSupportedType() {
        return PaymentTypeEnum.POINTS_PLUS_VOUCHER;
    }

    @Override
    public PaymentResult validate(PaymentRequest request) {
        if (request.getCashUsed() != null) {
            return PaymentResult.fail("points+voucher payment must not include cash");
        }
        if (request.getVoucherUsed() == null
                || request.getVoucherUsed().compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentResult.fail("points+voucher payment requires a positive voucher amount");
        }
        if (request.getPointsUsed() == null
                || request.getPointsUsed() < request.getProductMinPoints()) {
            return PaymentResult.fail("points must be at least the minimum: "
                    + request.getProductMinPoints());
        }
        if (request.getPointsUsed() >= request.getProductTotalPoints()) {
            return PaymentResult.fail("points already cover full price — use points-only instead");
        }
        if (request.getPointsUsed() > request.getUserCurrentPoint()) {
            return PaymentResult.fail("insufficient points: have "
                    + request.getUserCurrentPoint() + ", need " + request.getPointsUsed());
        }
        if (request.getVoucherUsed().compareTo(PaymentAmountCalculator.calculateSupplement(request)) != 0) {
            return PaymentResult.fail("voucher must equal the remaining points value at 1 point = 0.1 voucher");
        }
        return PaymentResult.ok();
    }
}