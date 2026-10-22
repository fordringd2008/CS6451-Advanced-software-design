package com.hotel.hotel_loyalty.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import lombok.AccessLevel;

/**
 * Outcome of a payment strategy validation (UC3 step 6a / Extensions 5a).
 * A strategy never touches the database — it only validates and reports.
 *
 * @author Fordring
 * @since 1.0
 */
@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResult {

    private final boolean success;
    private final String message;

    public static PaymentResult ok() {
        return new PaymentResult(true, "payment validated");
    }

    public static PaymentResult fail(String reason) {
        return new PaymentResult(false, reason);
    }
}