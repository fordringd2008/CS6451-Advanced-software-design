package com.hotel.hotel_loyalty.vo;

import com.hotel.hotel_loyalty.common.DateTimeDisplayFormatter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * One row in the "Points History" tab (GUI: my_points prototype Points History).
 * Audit log of every points earn/redeem event.
 *
 * @author wukewei
 * @since 1.0
 */
@Data
public class PointsHistoryVO {

    /** Transaction business key (GUI: "PT-..."). */
    private String ptid;

    /** Transaction type: "EARN" or "REDEEM". */
    private String type;

    /** Signed points delta: positive = earn, negative = redeem (GUI: "+200" / "-800"). */
    private Integer pointsChange;

    /** Related order business key, null for non-order earnings (GUI: "Order #90117"). */
    private String relatedOrderId;

    /** Transaction time (GUI: "2025-10-14"). */
    private LocalDateTime gmtCreate;

    public String getGmtCreateDisplay() {
        return DateTimeDisplayFormatter.format(gmtCreate);
    }
}