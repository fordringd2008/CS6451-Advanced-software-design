package com.hotel.hotel_loyalty.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * User data object, maps to table "user".
 * Fields aligned with candidate_objects_1.0 and ER diagram.
 *
 * @author wukewei
 * @since 1.0
 */
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "password")
public class UserDO {

    /** Auto-increment primary key (Alibaba rule: every table must have id). */
    private Long id;

    /** Business key — unique user identifier. */
    private String uid;

    /** User display name. */
    private String name;

    /** User password (hashed). */
    private String password;

    /** Account status: 0 Normal, 1 Inactive, 2 Forbidden (see UserStatusEnum). */
    private Integer status;

    /** User role: 0 Guest, 1 Manager (see UserRoleEnum). */
    private Integer role;

    /** Registration time. */
    private LocalDateTime registerTime;

    /** Membership level: 1-5 (see UserLevelEnum). */
    private Integer level;

    /** Current points balance. */
    private Integer currentPoint;

    /** Row creation time (Alibaba rule). */
    private LocalDateTime gmtCreate;

    /** Row last modification time (Alibaba rule). */
    private LocalDateTime gmtModified;
}