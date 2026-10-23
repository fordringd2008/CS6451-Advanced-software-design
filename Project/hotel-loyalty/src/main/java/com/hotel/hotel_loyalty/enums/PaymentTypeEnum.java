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
    POINTS_ONLY(1, "PM-1", "Points Only"),

    /** Type 2: Points plus cash combination payment. */
    POINTS_PLUS_CASH(2, "PM-2", "Points + Cash"),

    /** Type 3: Points plus voucher combination payment. */
    POINTS_PLUS_VOUCHER(3, "PM-3", "Points + Voucher");

    /** Numeric payment type code. */
    private final Integer code;
    /** Payment-method business key. */
    private final String pmid;
    /** Display description. */
    private final String description;

    PaymentTypeEnum(Integer code, String pmid, String description) {
        this.code = code;
        this.pmid = pmid;
        this.description = description;
    }

    /**
     * Returns the payment-method business key stored in the database.
     *
     * @return payment-method business key
     */
    public String getPmid() {
        return pmid;
    }

    /**
     * Returns the numeric payment type code.
     *
     * @return payment type code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Returns the display description.
     *
     * @return payment type description
     */
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

    /**
     * Resolves a payment type from its database business key.
     *
     * @param pmid payment-method business key
     * @return matching payment type, or null when no type matches
     */
    public static PaymentTypeEnum fromPmid(String pmid) {
        if (pmid == null) {
            return null;
        }
        for (PaymentTypeEnum type : values()) {
            if (type.pmid.equals(pmid)) {
                return type;
            }
        }
        return null;
    }
}