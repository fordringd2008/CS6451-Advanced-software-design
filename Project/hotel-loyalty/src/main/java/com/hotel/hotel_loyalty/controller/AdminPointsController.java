package com.hotel.hotel_loyalty.controller;

import com.hotel.hotel_loyalty.service.PointsAdjustmentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * UC5 page controller — renders the admin points-adjustment JSP.
 * Data is loaded via fetch (GET /api/admin/points/user/{uid}) per the
 * JSP+AJAX architecture (same pattern as AdminProductController).
 *
 * @author Fordring
 * @since 1.0
 */
@Controller
@RequestMapping("/admin/points")
public class AdminPointsController {

    private final PointsAdjustmentService pointsAdjustmentService;

    public AdminPointsController(PointsAdjustmentService pointsAdjustmentService) {
        this.pointsAdjustmentService = pointsAdjustmentService;
    }

    @GetMapping
    public String adminPoints() {
        return "admin_points_adjust";
    }
}