package com.hotel.hotel_loyalty.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Points rule data object, maps to table "points_rule".
 * <p>
 * Field definitions follow candidate_objects_1.0.xlsx (team decision 2026-10-22):
 * <ul>
 *   <li>"threshold" — minimum external order amount to apply the rule (INT).
 *       NULL for BY_LEVEL rules (no minimum; the rule applies by user level).</li>
 *   <li>"pointsRatio" — percentage (INT): order amount × ratio% = points earned.
 *       Level multipliers stored as percentages: L1=100, L2=120, L3=150, L4=180, L5=200.</li>
 *   <li>"startTime"/"endTime" — effective period for BY_PERIOD rules (type=2).</li>
 *   <li>"description" — rule description text.</li>
 * </ul>
 *
 * @author Fordring
 * @since 1.0
 */
@Data
@EqualsAndHashCode(of = "id")
public class PointsRuleDO {

    /** Auto-increment primary key. */
    private Long id;

    /** Business key — unique rule identifier. */
    private String prid;

    /** Rule display name. */
    private String name;

    /** Rule type code — see {@link com.hotel.hotel_loyalty.enums.PointsRuleTypeEnum}. */
    private Integer type;

    /** Minimum external order amount to apply the rule; NULL for BY_LEVEL (no minimum). */
    private Integer threshold;

    /** Percentage: order amount × ratio% = points (e.g. 150 = 150%). */
    private Integer pointsRatio;

    /** Effective period start — type=2 (BY_PERIOD) only. */
    private LocalDateTime startTime;

    /** Effective period end — type=2 (BY_PERIOD) only. */
    private LocalDateTime endTime;

    /** Rule description. */
    private String description;

    /** Row creation time (Alibaba rule). */
    private LocalDateTime gmtCreate;

    /** Row last modification time (Alibaba rule). */
    private LocalDateTime gmtModified;
}