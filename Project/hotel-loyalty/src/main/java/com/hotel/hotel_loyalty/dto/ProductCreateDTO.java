package com.hotel.hotel_loyalty.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * UC6 submitCreate form — fields match admin GUI product modal exactly.
 *
 * @author Fordring
 * @since 1.0
 */
@Data
public class ProductCreateDTO {

    /** Product display name (GUI: 商品名称*). */
    @NotBlank(message = "product name must not be blank")
    private String name;

    /** Product image URL (GUI: 图片URL*). */
    @NotBlank(message = "image URL must not be blank")
    private String imageUrl;

    /** Total points required (GUI: 所需总积分*). */
    @NotNull(message = "total points required must not be null")
    @Min(value = 1, message = "total points required must be positive")
    private Integer totalPointsRequired;

    /** Minimum points required (GUI: 最低需积分*, difference payable by cash/voucher). */
    @NotNull(message = "min points required must not be null")
    @Min(value = 1, message = "min points required must be positive")
    private Integer minPointsRequired;

    /** Total stock (GUI: 总库存*). On create, remainingStock = totalStock (UC6). */
    @NotNull(message = "total stock must not be null")
    @Min(value = 1, message = "total stock must be positive")
    private Integer totalStock;
}