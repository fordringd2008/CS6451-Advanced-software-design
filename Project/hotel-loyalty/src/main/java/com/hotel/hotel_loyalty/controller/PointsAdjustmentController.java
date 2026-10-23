package com.hotel.hotel_loyalty.controller;

import com.hotel.hotel_loyalty.common.Result;
import com.hotel.hotel_loyalty.dto.PointsAdjustmentDTO;
import com.hotel.hotel_loyalty.service.PointsAdjustmentService;
import com.hotel.hotel_loyalty.vo.UserPointsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UC5 Manually Add Points — admin REST endpoints.
 * <p>
 * Step 1 (verify user) is synchronous; steps 2-4 (submit → enqueue) return
 * immediately with a queue-accepted confirmation. Steps 5-6 (credit points,
 * create transaction) run asynchronously in the JMS consumer.
 *
 * @author Fordring
 * @since 1.0
 */
@Tag(name = "UC5 积分调整", description = "Admin manually add points — use case 5")
@RestController
@RequestMapping("/api/admin/points")
public class PointsAdjustmentController {

    private final PointsAdjustmentService pointsAdjustmentService;

    public PointsAdjustmentController(PointsAdjustmentService pointsAdjustmentService) {
        this.pointsAdjustmentService = pointsAdjustmentService;
    }

    @Operation(summary = "验证目标用户", description = "UC5 step 1 — look up user for the adjustment form")
    @GetMapping("/user/{uid}")
    public Result<UserPointsVO> getUser(
            @Parameter(description = "User business key", example = "U-001")
            @PathVariable String uid) {
        return Result.success(pointsAdjustmentService.getUserByUid(uid));
    }

    @Operation(summary = "提交加分",
            description = "UC5 steps 2-4 — validates user status then enqueues; consumer credits asynchronously")
    @PostMapping("/adjustment")
    public Result<String> adjust(@Valid @RequestBody PointsAdjustmentDTO dto) {
        return Result.success(pointsAdjustmentService.adjustPoints(dto));
    }
}