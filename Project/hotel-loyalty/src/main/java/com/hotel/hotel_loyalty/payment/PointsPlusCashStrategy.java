package com.hotel.hotel_loyalty.payment;

import com.hotel.hotel_loyalty.enums.PaymentTypeEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Type 2 — points + cash combination payment (UC3 Extension 5a).
 * Rules: points must meet the minimum, a positive cash supplement is required.
 *
 * @author wukewei
 * @since 1.0
 */
@Component
public class PointsPlusCashStrategy implements PaymentStrategy {

    @Override
    public PaymentTypeEnum getSupportedType() {
        return PaymentTypeEnum.POINTS_PLUS_CASH;
    }

    @Override
    public PaymentResult validate(PaymentRequest request) {
        if (request.getVoucherUsed() != null) {
            return PaymentResult.fail("points+cash payment must not include a voucher");
        }
        if (request.getCashUsed() == null || request.getCashUsed().compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentResult.fail("points+cash payment requires a positive cash amount");
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
        if (request.getCashUsed().compareTo(PaymentAmountCalculator.calculateSupplement(request)) != 0) {
            return PaymentResult.fail("cash must equal the remaining points value at 1 point = 0.1 cash");
        }
        return PaymentResult.ok();
    }
}