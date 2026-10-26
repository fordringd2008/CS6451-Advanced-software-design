package com.hotel.hotel_loyalty.messaging;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Sends points-adjustment messages to the persistent queue (UC5).
 * Delivery mode is persistent by default in JmsTemplate, satisfying the
 * "persistent queue" requirement.
 *
 * @author wukewei
 * @since 1.0
 */
@Component
public class PointsAdjustmentProducer {

    public static final String QUEUE = "queue.points.adjustment";

    private final JmsTemplate jmsTemplate;

    public PointsAdjustmentProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Enqueue an asynchronous points adjustment.
     *
     * @param uid            target user
     * @param pointsChange   signed delta (+ earn / - redeem)
     * @param type           "EARN" or "REDEEM"
     * @param relatedOrderId related order, nullable
     */
    public void send(String uid, Integer pointsChange, String type, String relatedOrderId) {
        PointsAdjustmentMessage message =
                new PointsAdjustmentMessage(uid, pointsChange, type, relatedOrderId);
        jmsTemplate.convertAndSend(QUEUE, message);
    }
}