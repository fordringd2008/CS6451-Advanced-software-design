package com.hotel.hotel_loyalty.payment;

/**
 * Abstraction over the external payment channel used to settle the non-points
 * portion of a combination payment.
 * <p>
 * Isolates the actual collection of cash / redemption of vouchers from the
 * redemption flow. The in-memory {@link StubPaymentGateway} only simulates a
 * successful settlement; swapping in a real provider (Stripe, Alipay, a
 * voucher-verification service, ...) requires no change to the calling code.
 *
 * @author Fordring
 * @since 1.0
 */
public interface PaymentGateway {

    /**
     * Charges the cash supplement of a points-plus-cash redemption (Type 2).
     *
     * @param request immutable payment request carrying uid, pid and cashUsed
     * @return gateway outcome (accepted / declined)
     */
    PaymentGatewayResult chargeCash(PaymentRequest request);

    /**
     * Redeems the voucher supplement of a points-plus-voucher redemption
     * (Type 3).
     *
     * @param request immutable payment request carrying uid, pid and voucherUsed
     * @return gateway outcome (accepted / declined)
     */
    PaymentGatewayResult redeemVoucher(PaymentRequest request);
}
