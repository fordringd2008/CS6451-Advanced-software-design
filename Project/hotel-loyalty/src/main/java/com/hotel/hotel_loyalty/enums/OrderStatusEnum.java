package com.hotel.hotel_loyalty.enums;

/**
 * Order status lifecycle.
 * Aligned with candidate_objects and order_state_diagram_1.0:
 * 1 Pending Payment, 2 Paid, 3 Completed, 5 Cancelled.
 * (Code 4 intentionally skipped per candidate_objects.)
 *
 * @author Fordring
 * @since 1.0
 */
public enum OrderStatusEnum {

    /** Order created, waiting for payment. No points deducted yet. */
    PENDING_PAYMENT(1, "Pending Payment"),

    /** Payment successful, points deducted, stock locked, awaiting shipment. */
    PAID(2, "Paid"),

    /** Order finished, no further changes allowed. */
    COMPLETED(3, "Completed"),

    /** Order cancelled. If previously paid, points refunded and stock restored. */
    CANCELLED(5, "Cancelled");

    private final Integer code;
    private final String description;

    OrderStatusEnum(Integer code, String description) {
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
     * @param code status code stored in database
     * @return matching enum, or null if not found
     */
    public static OrderStatusEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrderStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}