package com.hotel.hotel_loyalty.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * One row in the admin points-rule table (GUI: admin 积分规则 tab).
 * Columns: PRID | 类型 | 阈值 | 积分比例 | 描述 | 操作.
 * <p>
 * Field semantics follow candidate_objects_1.0.xlsx (team decision 2026-10-22):
 * threshold = min order amount (null for BY_LEVEL); pointsRatio = percentage.
 *
 * @author Fordring
 * @since 1.0
 */
@Data
public class RuleAdminVO {

    /** Rule business key. */
    private String prid;

    /** Rule display name. */
    private String name;

    /** Rule type code: 1 = by level, 2 = by period. */
    private Integer type;

    /** Minimum external order amount; null for BY_LEVEL rules (no minimum). */
    private Integer threshold;

    /** Percentage: order amount × ratio% = points (e.g. 150 = 150%). */
    private Integer pointsRatio;

    /** Effective period start — type=2 only. */
    private LocalDateTime startTime;

    /** Effective period end — type=2 only. */
    private LocalDateTime endTime;

    /** Rule description. */
    private String description;
}