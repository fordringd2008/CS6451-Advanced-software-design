package com.hotel.hotel_loyalty.enums;

/**
 * Payment method type for extensible combination payment.
 * Aligned with candidate_objects, UC3 sequence diagram and confirm_payment prototype:
 * 1 Pure Points, 2 Points + Cash, 3 Points + Voucher.
 * New types can be added without changing core flow (Extensibility).
 *
 * @author Fordring
 * @since 1.0
 */
public enum PaymentTypeEnum {

    /** Type 1: Pure points payment. */
    POINTS_ONLY(1, "Points Only"),

    /** Type 2: Points plus cash combination payment. */
    POINTS_PLUS_CASH(2, "Points + Cash"),

    /** Type 3: Points plus voucher combination payment. */
    POINTS_PLUS_VOUCHER(3, "Points + Voucher");

    private final Integer code;
    private final String description;

    PaymentTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Resolve enum by code value.
     *
     * @param code payment type code stored in database
     * @return matching enum, or null if not found
     */
    public static PaymentTypeEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PaymentTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}