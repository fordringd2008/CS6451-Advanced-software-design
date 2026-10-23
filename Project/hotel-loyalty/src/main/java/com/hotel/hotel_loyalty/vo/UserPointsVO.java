package com.hotel.hotel_loyalty.vo;

import lombok.Data;

/**
 * User points summary for the admin points-adjustment page (UC5 step 1).
 * Excludes password — never exposed to the web layer.
 *
 * @author Fordring
 * @since 1.0
 */
@Data
public class UserPointsVO {

    /** User business key. */
    private String uid;

    /** User display name. */
    private String name;

    /** Account status: 0 Normal, 1 Inactive, 2 Forbidden. */
    private Integer status;

    /** Membership level: 1-5 (L1..L5). */
    private Integer level;

    /** Current points balance. */
    private Integer currentPoint;
}