package com.hotel.hotel_loyalty.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * UC6 submitCreate form — fields match admin GUI product modal exactly.
 *
 * @author wukewei
 * @since 1.0
 */
@Data
public class ProductCreateDTO {

    /** Product display name (GUI: product name*). */
    @NotBlank(message = "product name must not be blank")
    private String name;

    /** Product image URL (GUI: image URL*). */
    @NotBlank(message = "image URL must not be blank")
    @Size(max = 2048, message = "image URL must not exceed 2048 characters")
    private String imageUrl;

    /** Total points required (GUI: total points required*). */
    @NotNull(message = "total points required must not be null")
    @Min(value = 1, message = "total points required must be positive")
    private Integer totalPointsRequired;

    /** Minimum points required (GUI: min points required*, difference payable by cash/voucher). */
    @NotNull(message = "min points required must not be null")
    @Min(value = 1, message = "min points required must be positive")
    private Integer minPointsRequired;

    /** Total stock (GUI: total stock*). On create, remainingStock = totalStock (UC6). */
    @NotNull(message = "total stock must not be null")
    @Min(value = 1, message = "total stock must be positive")
    private Integer totalStock;
}