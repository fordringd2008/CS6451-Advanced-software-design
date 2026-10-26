package com.hotel.hotel_loyalty.enums;

/**
 * Membership level (L1 to L5).
 * Aligned with candidate_objects (level 1-5), my_points prototype and admin rules.
 *
 * @author wukewei
 * @since 1.0
 */
public enum UserLevelEnum {

    /** Level 1 — basic member. */
    L1(1, "Level 1"),

    /** Level 2 — bronze member. */
    L2(2, "Level 2"),

    /** Level 3 — silver member. */
    L3(3, "Level 3"),

    /** Level 4 — gold member. */
    L4(4, "Level 4"),

    /** Level 5 — platinum member. */
    L5(5, "Level 5");

    private final Integer code;
    private final String description;

    UserLevelEnum(Integer code, String description) {
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
     * @param code level code stored in database
     * @return matching enum, or null if not found
     */
    public static UserLevelEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserLevelEnum level : values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
        return null;
    }
}