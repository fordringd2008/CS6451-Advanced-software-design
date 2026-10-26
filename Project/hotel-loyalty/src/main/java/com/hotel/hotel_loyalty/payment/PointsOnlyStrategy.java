package com.hotel.hotel_loyalty.payment;

import com.hotel.hotel_loyalty.enums.PaymentTypeEnum;
import org.springframework.stereotype.Component;

/**
 * Type 1 — pure points payment (UC3 main flow).
 * Rules: points must cover the full price, no cash/voucher supplement.
 *
 * @author wukewei
 * @since 1.0
 */
@Component
public class PointsOnlyStrategy implements PaymentStrategy {

    @Override
    public PaymentTypeEnum getSupportedType() {
        return PaymentTypeEnum.POINTS_ONLY;
    }

    @Override
    public PaymentResult validate(PaymentRequest request) {
        if (request.getCashUsed() != null || request.getVoucherUsed() != null) {
            return PaymentResult.fail("points-only payment must not include cash or voucher");
        }
        if (request.getPointsUsed() == null
                || !request.getPointsUsed().equals(request.getProductTotalPoints())) {
            return PaymentResult.fail("points must equal the full product price: "
                    + request.getProductTotalPoints());
        }
        if (request.getPointsUsed() > request.getUserCurrentPoint()) {
            return PaymentResult.fail("insufficient points: have "
                    + request.getUserCurrentPoint() + ", need " + request.getPointsUsed());
        }
        return PaymentResult.ok();
    }
}