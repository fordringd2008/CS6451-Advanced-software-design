package com.hotel.hotel_loyalty.payment;

import com.hotel.hotel_loyalty.enums.PaymentTypeEnum;

/**
 * Strategy interface for redemption payment validation (UC3).
 * <p>
 * Extensibility: adding a new payment type (e.g. Points + Credit) only
 * requires a new implementation and a factory entry — no existing code
 * changes (fulfils the use-case diagram Extensibility note).
 *
 * @author wukewei
 * @since 1.0
 */
public interface PaymentStrategy {

    /** Which payment type this strategy handles. */
    PaymentTypeEnum getSupportedType();

    /** Validate the payment request against this strategy's rules. */
    PaymentResult validate(PaymentRequest request);
}