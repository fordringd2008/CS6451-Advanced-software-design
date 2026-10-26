package com.hotel.hotel_loyalty.messaging;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * JMS message payload for asynchronous points adjustment (UC5).
 * <p>
 * UC5 requirement: "Uses persistent queue + automatic retry to ensure
 * high availability." The broker (embedded Artemis) persists this message;
 * the consumer retries on failure until it succeeds.
 * <p>
 * Note: constructor is hand-written (not Lombok @RequiredArgsConstructor)
 * because Lombok-generated constructor parameter names are not preserved
 * in bytecode, which breaks Jackson deserialization in the JMS converter.
 *
 * @author wukewei
 * @since 1.0
 */
@Getter
@ToString
public class PointsAdjustmentMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** User whose points change. */
    private final String uid;
    /** Signed delta: positive = earn, negative = redeem. */
    private final Integer pointsChange;
    /** "EARN" or "REDEEM". */
    private final String type;
    /** Related order id, nullable for non-order earnings. */
    private final String relatedOrderId;

    public PointsAdjustmentMessage(String uid, Integer pointsChange, String type, String relatedOrderId) {
        this.uid = uid;
        this.pointsChange = pointsChange;
        this.type = type;
        this.relatedOrderId = relatedOrderId;
    }
}