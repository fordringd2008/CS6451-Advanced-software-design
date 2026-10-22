package com.hotel.hotel_loyalty.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Points rule data object, maps to table "points_rule".
 * <p>
 * Design decisions (consistency audit):
 * <ul>
 *   <li>"pointsRatio" uses BigDecimal (decimal(4,1) in DDL) — candidate_objects
 *       defines it as int 1-100 percentage, but level multipliers like L3 = 1.5x
 *       cannot be stored as an int ratio. BigDecimal covers both percentage
 *       (BY_PERIOD) and multiplier (BY_LEVEL) semantics.</li>
 *   <li>"threshold" kept as Integer with dual semantics pending team confirmation:
 *       BY_LEVEL → user level (1-5), BY_PERIOD → minimum spend amount.
 *       (candidate_objects description contradicts PointsRuleTypeEnum.)</li>
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

    /** Threshold: level number (BY_LEVEL) or minimum spend amount (BY_PERIOD). */
    private Integer threshold;

    /** Points ratio: multiplier (BY_LEVEL, e.g. 1.5) or percentage (BY_PERIOD, e.g. 100). */
    private BigDecimal pointsRatio;

    /** Row creation time (Alibaba rule). */
    private LocalDateTime gmtCreate;

    /** Row last modification time (Alibaba rule). */
    private LocalDateTime gmtModified;
}