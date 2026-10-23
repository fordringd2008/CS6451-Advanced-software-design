package com.hotel.hotel_loyalty.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * UC5 manual points-credit form — fields match admin GUI adjust tab step 2.
 *
 * @author Fordring
 * @since 1.0
 */
@Data
public class PointsAdjustDTO {

    /** Target user business key (GUI: enter target user UID). */
    @NotBlank(message = "uid must not be blank")
    private String uid;

    /** Points to credit, positive integer only (GUI: points amount*, positive integer). */
    @NotNull(message = "points amount must not be null")
    @Min(value = 1, message = "points amount must be a positive integer")
    private Integer pointsAmount;

    /** Adjustment reason, optional (GUI: reason, optional).
     * Note: only appears in GUI, not in candidate_objects/ER — kept here
     * as transport field; persistence of reason is deferred. */
    private String reason;
}