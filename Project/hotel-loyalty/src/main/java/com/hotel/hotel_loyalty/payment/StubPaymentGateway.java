package com.hotel.hotel_loyalty.payment;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * In-memory payment gateway stub.
 * <p>
 * Simulates a successful cash charge / voucher redemption without contacting
 * any real payment provider. It is the default {@link PaymentGateway} bean and
 * the single seam for wiring a production gateway later — replace this bean
 * only, no caller changes.
 *
 * @author Fordring
 * @since 1.0
 */
@Component
public class StubPaymentGateway implements PaymentGateway {

    /** Prefix for the fake transaction reference issued on success. */
    private static final String REFERENCE_PREFIX = "STUB-";
    /** Number of UUID characters kept in the fake transaction reference. */
    private static final int REFERENCE_SUFFIX_LENGTH = 8;

    @Override
    public PaymentGatewayResult chargeCash(PaymentRequest request) {
        return PaymentGatewayResult.ok(newReference());
    }

    @Override
    public PaymentGatewayResult redeemVoucher(PaymentRequest request) {
        return PaymentGatewayResult.ok(newReference());
    }

    /**
     * Builds a fake gateway transaction reference.
     *
     * @return stub transaction reference
     */
    private String newReference() {
        return REFERENCE_PREFIX + UUID.randomUUID().toString()
                .substring(0, REFERENCE_SUFFIX_LENGTH).toUpperCase();
    }
}
