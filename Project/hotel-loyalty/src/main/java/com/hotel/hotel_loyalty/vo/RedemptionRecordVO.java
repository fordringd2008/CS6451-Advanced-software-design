package com.hotel.hotel_loyalty.vo;

import com.hotel.hotel_loyalty.common.DateTimeDisplayFormatter;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * One row in the "My Redemptions" tab (GUI: my_points record cards).
 * Example: "Custom Hotel LEGO Keychain | Order #90117 | -800 pts +€30 | 2025-10-14".
 *
 * @author Fordring
 * @since 1.0
 */
@Data
public class RedemptionRecordVO {

    /** Order business key (GUI: "Order #90117"). */
    private String oid;

    /** Product business key (for image lookup). */
    private String pid;

    /** Product name (GUI: "Custom Hotel LEGO Keychain"). */
    private String productName;

    /** Points consumed (GUI: "-800 pts"). */
    private Integer pointsUsed;

    /** Cash supplement, null if points-only (GUI: "+€30"). */
    private BigDecimal cashUsed;

    /** Voucher supplement, null if not applicable. */
    private BigDecimal voucherUsed;

    /** Order status code — see {@link com.hotel.hotel_loyalty.enums.OrderStatusEnum}. */
    private Integer status;

    /** Human-readable status label (e.g. "Paid") — derived from the code at mapping time. */
    private String statusLabel;

    /** Order creation time (GUI: "2025-10-14"). */
    private LocalDateTime gmtCreate;

    public String getGmtCreateDisplay() {
        return DateTimeDisplayFormatter.format(gmtCreate);
    }
}