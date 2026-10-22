package com.hotel.hotel_loyalty.enums;

/**
 * Points rule type.
 * Aligned with candidate_objects and admin prototype:
 * 1 Based on User Level, 2 Based on Specific Time Period.
 *
 * @author Fordring
 * @since 1.0
 */
public enum PointsRuleTypeEnum {

    /** Type 1: Rule applies based on user membership level (threshold = level value 1-5). */
    BY_LEVEL(1, "Based on User Level"),

    /** Type 2: Rule applies during a specific time period (threshold = period identifier). */
    BY_PERIOD(2, "Based on Time Period");

    private final Integer code;
    private final String description;

    PointsRuleTypeEnum(Integer code, String description) {
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
     * @param code rule type code stored in database
     * @return matching enum, or null if not found
     */
    public static PointsRuleTypeEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PointsRuleTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}