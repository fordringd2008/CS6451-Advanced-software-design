package com.hotel.hotel_loyalty.controller;

import com.hotel.hotel_loyalty.common.Result;
import com.hotel.hotel_loyalty.dto.RedemptionRequest;
import com.hotel.hotel_loyalty.service.GuestPointsService;
import com.hotel.hotel_loyalty.vo.ConfirmPaymentVO;
import com.hotel.hotel_loyalty.vo.AccountVO;
import com.hotel.hotel_loyalty.vo.MyPointsVO;
import com.hotel.hotel_loyalty.vo.PointsMallVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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

    /**
     * IDOR defense: the uid in the request must match the session identity.
     * <p>
     * Same rule as {@link GuestJspController#checkOwnership} — the REST API
     * must not rely on the JSP layer alone, because anyone can call the API
     * directly with a different uid (OWASP: never trust client-supplied
     * identifiers).
     *
     * @param requestUid uid from the URL path or request body
     * @throws AccessDeniedException if the uid does not match
     */
    private void checkOwnership(String requestUid) {
        String sessionUid = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        if (!requestUid.equals(sessionUid)) {
            throw new AccessDeniedException("Not your resource");
        }
    }

    @Operation(summary = "Load points mall")
    @GetMapping("/{uid}/points-mall")
    public Result<PointsMallVO> pointsMall(@PathVariable @NotBlank String uid) {
        checkOwnership(uid);
        return Result.success(guestPointsService.getPointsMall(uid));
    }

    @Operation(summary = "Load my points")
    @GetMapping("/{uid}/my-points")
    public Result<MyPointsVO> myPoints(@PathVariable @NotBlank String uid) {
        checkOwnership(uid);
        return Result.success(guestPointsService.getMyPoints(uid));
    }

    @Operation(summary = "Load account details")
    @GetMapping("/{uid}/account")
    public Result<AccountVO> account(@PathVariable @NotBlank String uid) {
        checkOwnership(uid);
        return Result.success(guestPointsService.getAccount(uid));
    }

    @Operation(summary = "Load payment confirmation")
    @GetMapping("/{uid}/confirm-payment/{pid}")
    public Result<ConfirmPaymentVO> confirmPayment(@PathVariable @NotBlank String uid,
                                                    @PathVariable @NotBlank String pid) {
        checkOwnership(uid);
        return Result.success(guestPointsService.getConfirmPayment(uid, pid));
    }

    @Operation(summary = "Redeem a product")
    @PostMapping("/redemptions")
    public Result<String> redeem(@Valid @RequestBody RedemptionRequest request) {
        checkOwnership(request.getUid());
        return Result.success(guestPointsService.redeem(request));
    }
}
