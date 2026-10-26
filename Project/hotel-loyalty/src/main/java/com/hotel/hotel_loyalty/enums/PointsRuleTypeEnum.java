package com.hotel.hotel_loyalty.enums;

/**
 * Points rule type.
 * Aligned with candidate_objects_1.0.xlsx (team decision 2026-10-22):
 * 1 Based on User Level, 2 Based on Specific Time Period.
 * <p>
 * Threshold semantics (candidate_objects): minimum external order amount
 * to apply the rule. For BY_LEVEL, threshold is NULL (no minimum — the rule
 * applies by user level). For BY_PERIOD, threshold is the min order amount
 * and startTime/endTime define the effective period.
 *
 * @author wukewei
 * @since 1.0
 */
public enum PointsRuleTypeEnum {

    /** Type 1: Rule applies based on user membership level (threshold = NULL, no minimum). */
    BY_LEVEL(1, "Based on User Level"),

    /** Type 2: Rule applies during a specific time period (threshold = min order amount). */
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