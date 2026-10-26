package com.hotel.hotel_loyalty.vo;

import lombok.Data;

/**
 * Payment method radio option on the confirm-payment page (GUI: Type 1/2/3).
 *
 * @author wukewei
 * @since 1.0
 */
@Data
public class PaymentMethodVO {

    /** Payment method business key (PM-1 / PM-2 / PM-3). */
    private String pmid;

    /** Display label, e.g. "Points Only (Type 1)". */
    private String name;
}