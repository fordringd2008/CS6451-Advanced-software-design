package com.hotel.hotel_loyalty.service;

import com.hotel.hotel_loyalty.dto.PointsAdjustmentDTO;
import com.hotel.hotel_loyalty.vo.UserPointsVO;

/**
 * UC5 Manually Add Points — service layer.
 * <p>
 * Flow (use case steps 1-6): verify target user (precondition 2) →
 * enqueue the adjustment (step 4) → consumer credits points asynchronously
 * (steps 5-6, with automatic retry).
 *
 * @author wukewei
 * @since 1.0
 */
public interface PointsAdjustmentService {

    /**
     * UC5 step 1: look up the target user for verification.
     *
     * @param uid target user business key
     * @return user points summary (no password)
     * @throws IllegalArgumentException if the user does not exist
     */
    UserPointsVO getUserByUid(String uid);

    /**
     * UC5 steps 2-4: validate user status, then enqueue the points adjustment.
     * The consumer credits currentPoint and creates a PointsTransaction
     * asynchronously (steps 5-6).
     *
     * @param dto adjustment request (uid, points, optional reason)
     * @return confirmation message for the GUI queue-status area
     * @throws IllegalArgumentException if the user does not exist
     * @throws IllegalStateException    if the user status is not normal (precondition 2)
     */
    String adjustPoints(PointsAdjustmentDTO dto);
}