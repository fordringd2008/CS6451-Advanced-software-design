package com.hotel.hotel_loyalty.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * My-points page header payload (GUI: user card + points card + register time).
 *
 * @author Fordring
 * @since 1.0
 */
@Data
public class MyPointsVO {

    /** User display name (GUI: "🔒 Wang Xiaoming"). */
    private String name;

    /** Membership level label (GUI: "Member Level: L3"). */
    private String levelLabel;

    /** Current points balance (GUI: "2,450 pts"). */
    private Integer currentPoint;

    /** Registration time (GUI: "Registered 2024-06-15"). */
    private LocalDateTime registerTime;
}