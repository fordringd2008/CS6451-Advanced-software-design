package com.hotel.hotel_loyalty.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * UC6 submitUpdate form — same fields as create plus pid (business key).
 *
 * @author wukewei
 * @since 1.0
 */
@Data
public class ProductUpdateDTO {

    /**
     * Business key — which product to update.
     * No @NotBlank: in the REST API the pid always comes from the path
     * variable ({@code PUT /{pid}}) and the Controller sets it after binding;
     * requiring it in the body would force the client to send a redundant field.
     */
    private String pid;

    /** Product display name. */
    @NotBlank(message = "product name must not be blank")
    private String name;

    /** Product image URL. */
    @NotBlank(message = "image URL must not be blank")
    @Size(max = 2048, message = "image URL must not exceed 2048 characters")
    private String imageUrl;

    /** Total points required. */
    @NotNull(message = "total points required must not be null")
    @Min(value = 1, message = "total points required must be positive")
    private Integer totalPointsRequired;

    /** Minimum points required. */
    @NotNull(message = "min points required must not be null")
    @Min(value = 1, message = "min points required must be positive")
    private Integer minPointsRequired;

    /** Total stock. */
    @NotNull(message = "total stock must not be null")
    @Min(value = 1, message = "total stock must be positive")
    private Integer totalStock;

    /** Remaining stock — manager may correct it during update. */
    @NotNull(message = "remaining stock must not be null")
    @Min(value = 0, message = "remaining stock must not be negative")
    private Integer remainingStock;
}