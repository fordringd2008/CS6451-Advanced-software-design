package com.hotel.hotel_loyalty.enums;

/**
 * User role.
 * Aligned with candidate_objects: 0 Guest, 1 Manager.
 *
 * @author wukewei
 * @since 1.0
 */
public enum UserRoleEnum {

    /** Guest — regular user who redeems points. */
    GUEST(0, "Guest"),

    /** Manager — admin who manages rules, products and points adjustment. */
    MANAGER(1, "Manager");

    private final Integer code;
    private final String description;

    UserRoleEnum(Integer code, String description) {
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
     * @param code role code stored in database
     * @return matching enum, or null if not found
     */
    public static UserRoleEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserRoleEnum role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return null;
    }
}