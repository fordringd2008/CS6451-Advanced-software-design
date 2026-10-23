package com.hotel.hotel_loyalty.vo;

import lombok.Data;

/**
 * One row in the admin product table (GUI: admin 商品管理 tab).
 * Columns: PID | 名称 | 所需积分 | 最低积分 | 库存(剩/总) | 操作.
 * imageUrl is not shown in the table but is needed to pre-fill the edit modal.
 *
 * @author Fordring
 * @since 1.0
 */
@Data
public class ProductAdminVO {

    /** Product business key. */
    private String pid;

    /** Product display name. */
    private String name;

    /** Product image URL (needed to pre-fill the edit modal). */
    private String imageUrl;

    /** Total points required. */
    private Integer totalPointsRequired;

    /** Minimum points required. */
    private Integer minPointsRequired;

    /** Remaining stock. */
    private Integer remainingStock;

    /** Total stock. */
    private Integer totalStock;
}