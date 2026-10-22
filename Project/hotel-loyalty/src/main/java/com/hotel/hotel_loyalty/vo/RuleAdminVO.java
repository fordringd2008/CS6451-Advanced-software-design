package com.hotel.hotel_loyalty.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * One row in the admin points-rule table (GUI: admin 积分规则 tab).
 * Columns: PRID | 类型 | 阈值 | 积分比例 | 描述 | 操作.
 * <p>
 * Note: "threshold" is presented as-is from the DO. Its display meaning
 * (level number vs time-period identifier vs min spend) is pending team
 * decision — see consistency conflict #10.
 *
 * @author Fordring
 * @since 1.0
 */
@Data
public class RuleAdminVO {

    /** Rule business key. */
    private String prid;

    /** Rule type code: 1 = by level, 2 = by period. */
    private Integer type;

    /** Threshold raw value — semantics depend on type (conflict #10 unresolved). */
    private Integer threshold;

    /** Points ratio: multiplier (1.5) or percentage (100). */
    private BigDecimal pointsRatio;

    /** Rule description / display name. */
    private String name;
}