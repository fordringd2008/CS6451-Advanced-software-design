package com.hotel.hotel_loyalty.vo;

import lombok.Data;

/**
 * Guest-facing product card in the points mall (GUI: points_mall grid).
 *
 * @author wukewei
 * @since 1.0
 */
@Data
public class ProductVO {

    /** Product business key. */
    private String pid;

    /** Product display name. */
    private String name;

    /** Product image URL. */
    private String imageUrl;

    /** Points price shown on the card (e.g. "800 pts"). */
    private Integer totalPointsRequired;
}