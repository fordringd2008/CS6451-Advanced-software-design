package com.hotel.hotel_loyalty.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * UC4 rule create/update form — fields match admin GUI rule modal exactly.
 * Used for both create (POST) and update (PUT); prid comes from the path on update.
 * <p>
 * Field semantics follow candidate_objects_1.0.xlsx:
 * threshold = min order amount (nullable); pointsRatio = percentage (int).
 *
 * @author wukewei
 * @since 1.0
 */
@Data
public class PointsRuleDTO {

    /** Rule display name (GUI: rule name). */
    @NotBlank(message = "rule name must not be blank")
    private String name;

    /** Rule type: 1 = by user level, 2 = by time period (GUI: rule type*). */
    @NotNull(message = "rule type must not be null")
    private Integer type;

    /** Minimum external order amount to apply the rule; null for BY_LEVEL (GUI: threshold). */
    @Min(value = 0, message = "threshold must not be negative")
    private Integer threshold;

    /** Percentage: order amount × ratio% = points, e.g. 150 = 150% (GUI: points ratio*). */
    @NotNull(message = "points ratio must not be null")
    @Min(value = 1, message = "points ratio must be at least 1")
    private Integer pointsRatio;

    /** Effective period start — type=2 only (GUI: start time). */
    private LocalDateTime startTime;

    /** Effective period end — type=2 only (GUI: end time). */
    private LocalDateTime endTime;

    /** Rule description (GUI: description, optional). */
    private String description;
}