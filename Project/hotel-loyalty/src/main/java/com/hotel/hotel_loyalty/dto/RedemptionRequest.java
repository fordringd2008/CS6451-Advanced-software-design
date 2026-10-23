package com.hotel.hotel_loyalty.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Guest redemption request submitted by the confirm-payment page.
 *
 * @author Copilot
 * @since 1.0
 */
@Data
public class RedemptionRequest {

    /** Guest business key. */
    @NotBlank
    private String uid;

    /** Product business key. */
    @NotBlank
    private String pid;

    /** Payment-method business key. */
    @NotBlank
    private String pmid;

    /** Number of points to spend. */
    @NotNull
    @PositiveOrZero
    private Integer pointsUsed;

    /** Cash supplement for points-plus-cash payments. */
    @PositiveOrZero
    private BigDecimal cashUsed;

    /** Voucher supplement for points-plus-voucher payments. */
    @PositiveOrZero
    private BigDecimal voucherUsed;
}
