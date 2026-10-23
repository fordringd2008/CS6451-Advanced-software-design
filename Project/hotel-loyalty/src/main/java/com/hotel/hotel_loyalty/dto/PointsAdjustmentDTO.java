package com.hotel.hotel_loyalty.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * UC5 submitAdjustment form — fields match admin GUI points-adjustment step 2 exactly.
 *
 * @author Fordring
 * @since 1.0
 */
@Data
public class PointsAdjustmentDTO {

    /** Target user business key (GUI step 1: select target user). */
    @NotBlank(message = "uid must not be blank")
    private String uid;

    /** Points to add — positive integer (GUI step 2: points amount*). */
    @NotNull(message = "points must not be null")
    @Min(value = 1, message = "points must be a positive integer")
    private Integer points;

    /** Optional adjustment reason (GUI step 2: reason, use case: "optional reason"). */
    private String reason;
}