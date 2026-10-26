package com.hotel.hotel_loyalty.vo;

import lombok.Data;

import java.util.List;

/**
 * Points mall page payload (GUI: header balance + product grid).
 *
 * @author wukewei
 * @since 1.0
 */
@Data
public class PointsMallVO {

    /** Guest's current points balance (GUI: "Current Points: 2,450"). */
    private Integer currentPoint;

    /** All products available for redemption. */
    private List<ProductVO> products;
}