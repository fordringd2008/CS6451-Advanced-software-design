package com.hotel.hotel_loyalty.enums;

/**
 * User account status.
 * Aligned with candidate_objects: 0 Normal, 1 Inactive, 2 Forbidden.
 *
 * @author wukewei
 * @since 1.0
 */
public enum UserStatusEnum {

    /** Normal status — user can log in and perform all operations. */
    NORMAL(0, "Normal"),

    /** Inactive status — user cannot perform operations. */
    INACTIVE(1, "Inactive"),

    /** Forbidden status — user is banned. */
    FORBIDDEN(2, "Forbidden");

    private final Integer code;
    private final String description;

    UserStatusEnum(Integer code, String description) {
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
    public static UserStatusEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}