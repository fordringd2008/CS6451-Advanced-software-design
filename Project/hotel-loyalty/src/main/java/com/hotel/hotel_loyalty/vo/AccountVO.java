package com.hotel.hotel_loyalty.vo;

import com.hotel.hotel_loyalty.common.DateTimeDisplayFormatter;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Account details page payload (GUI: account profile card).
 *
 * @author wukewei
 * @since 1.0
 */
@Data
public class AccountVO {

    /** Business key — unique user identifier. */
    private String uid;

    /** User display name. */
    private String name;

    /** User role code: 0 Guest, 1 Manager (see UserRoleEnum). */
    private Integer role;

    /** Role display label (GUI: "Guest"). */
    private String roleLabel;

    /** Account status code: 0 Normal, 1 Inactive, 2 Forbidden (see UserStatusEnum). */
    private Integer status;

    /** Status display label (GUI: "Normal"). */
    private String statusLabel;

    /** Membership level label (GUI: "L2"). */
    private String levelLabel;

    /** Current points balance. */
    private Integer currentPoint;

    /** Registration time. */
    private LocalDateTime registerTime;

    public String getRegisterTimeDisplay() {
        return DateTimeDisplayFormatter.format(registerTime);
    }
}