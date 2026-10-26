package com.hotel.hotel_loyalty.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Points mall product data object, maps to table "product".
 * Fields aligned with candidate_objects_1.0, ER diagram and UC6.
 *
 * @author wukewei
 * @since 1.0
 */
@Data
@EqualsAndHashCode(of = "id")
public class ProductDO {

    /** Auto-increment primary key. */
    private Long id;

    /** Business key — unique product identifier. */
    private String pid;

    /** Product display name. */
    private String name;

    /** Product image URL. */
    private String imageUrl;

    /** Total points required to redeem this product. */
    private Integer totalPointsRequired;

    /** Minimum points required (remaining can be supplemented with cash/voucher). */
    private Integer minPointsRequired;

    /** Total stock quantity. */
    private Integer totalStock;

    /** Remaining current stock. On create, remainingStock = totalStock (UC6). */
    private Integer remainingStock;

    /** Row creation time (Alibaba rule). */
    private LocalDateTime gmtCreate;

    /** Row last modification time (Alibaba rule). */
    private LocalDateTime gmtModified;
}