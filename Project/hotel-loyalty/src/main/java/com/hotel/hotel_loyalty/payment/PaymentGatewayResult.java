package com.hotel.hotel_loyalty.payment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Outcome of a payment-gateway operation (cash charge or voucher redemption).
 * <p>
 * Produced by a {@link PaymentGateway} implementation. The stub gateway always
 * reports success; a production gateway may report failure (declined,
 * insufficient funds, voucher already redeemed, etc.).
 *
 * @author Fordring
 * @since 1.0
 */
@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentGatewayResult {

    /** Whether the gateway accepted the payment. */
    private final boolean success;

    /** Human-readable outcome, e.g. "payment accepted". */
    private final String message;

    /** Gateway transaction reference (a fake identifier for the stub gateway). */
    private final String gatewayReference;

    /**
     * Builds a successful gateway result carrying the transaction reference.
     *
     * @param gatewayReference transaction reference issued by the gateway
     * @return successful result
     */
    public static PaymentGatewayResult ok(String gatewayReference) {
        return new PaymentGatewayResult(true, "payment accepted", gatewayReference);
    }

    /**
     * Builds a failed gateway result.
     *
     * @param reason why the gateway rejected the payment
     * @return failed result
     */
    public static PaymentGatewayResult fail(String reason) {
        return new PaymentGatewayResult(false, reason, null);
    }
}
