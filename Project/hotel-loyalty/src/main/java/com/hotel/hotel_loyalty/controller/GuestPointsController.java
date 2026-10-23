package com.hotel.hotel_loyalty.controller;

import com.hotel.hotel_loyalty.common.Result;
import com.hotel.hotel_loyalty.dto.RedemptionRequest;
import com.hotel.hotel_loyalty.service.GuestPointsService;
import com.hotel.hotel_loyalty.vo.ConfirmPaymentVO;
import com.hotel.hotel_loyalty.vo.MyPointsVO;
import com.hotel.hotel_loyalty.vo.PointsMallVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for guest points and product redemption operations.
 *
 * @author Copilot
 * @since 1.0
 */
@Tag(name = "Guest points", description = "Points mall, points profile and product redemption")
@RestController
@RequestMapping("/api/guest")
@Validated
public class GuestPointsController {

    /** Guest points application service. */
    private final GuestPointsService guestPointsService;

    /**
     * Creates a guest points controller.
     *
     * @param guestPointsService guest points application service
     */
    public GuestPointsController(GuestPointsService guestPointsService) {
        this.guestPointsService = guestPointsService;
    }

    @Operation(summary = "Load points mall")
    @GetMapping("/{uid}/points-mall")
    public Result<PointsMallVO> pointsMall(@PathVariable @NotBlank String uid) {
        return Result.success(guestPointsService.getPointsMall(uid));
    }

    @Operation(summary = "Load my points")
    @GetMapping("/{uid}/my-points")
    public Result<MyPointsVO> myPoints(@PathVariable @NotBlank String uid) {
        return Result.success(guestPointsService.getMyPoints(uid));
    }

    @Operation(summary = "Load payment confirmation")
    @GetMapping("/{uid}/confirm-payment/{pid}")
    public Result<ConfirmPaymentVO> confirmPayment(@PathVariable @NotBlank String uid,
                                                    @PathVariable @NotBlank String pid) {
        return Result.success(guestPointsService.getConfirmPayment(uid, pid));
    }

    @Operation(summary = "Redeem a product")
    @PostMapping("/redemptions")
    public Result<String> redeem(@Valid @RequestBody RedemptionRequest request) {
        return Result.success(guestPointsService.redeem(request));
    }
}
