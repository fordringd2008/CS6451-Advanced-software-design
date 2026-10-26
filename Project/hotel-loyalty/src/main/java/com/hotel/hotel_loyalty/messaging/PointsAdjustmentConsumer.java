package com.hotel.hotel_loyalty.messaging;

import com.hotel.hotel_loyalty.domain.PointsTransactionDO;
import com.hotel.hotel_loyalty.domain.UserDO;
import com.hotel.hotel_loyalty.mapper.PointsTransactionMapper;
import com.hotel.hotel_loyalty.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Consumes points-adjustment messages from the persistent queue (UC5).
 * <p>
 * Retry semantics: any exception thrown from the listener rolls the message
 * back to the queue (Artemis redelivery), satisfying "automatic retry".
 * The optimistic-lock update (compareAndUpdatePoint) makes concurrent
 * adjustments safe.
 *
 * @author wukewei
 * @since 1.0
 */
@Component
public class PointsAdjustmentConsumer {

    private static final Logger log = LoggerFactory.getLogger(PointsAdjustmentConsumer.class);

    private final UserMapper userMapper;
    private final PointsTransactionMapper transactionMapper;

    public PointsAdjustmentConsumer(UserMapper userMapper, PointsTransactionMapper transactionMapper) {
        this.userMapper = userMapper;
        this.transactionMapper = transactionMapper;
    }

    @JmsListener(destination = PointsAdjustmentProducer.QUEUE)
    public void onMessage(PointsAdjustmentMessage message) {
        log.info("consuming points adjustment: {}", message);

        UserDO user = userMapper.selectByUid(message.getUid());
        if (user == null) {
            throw new IllegalStateException("user not found: " + message.getUid());
        }

        int newPoint = user.getCurrentPoint() + message.getPointsChange();
        if (newPoint < 0) {
            throw new IllegalStateException("points would go negative for user " + message.getUid()
                    + ": have " + user.getCurrentPoint() + ", change " + message.getPointsChange());
        }

        LocalDateTime now = LocalDateTime.now();
        int updated = userMapper.compareAndUpdatePoint(
                message.getUid(), user.getCurrentPoint(), newPoint, now);
        if (updated == 0) {
            // Concurrent modification — force redelivery to retry with fresh balance.
            throw new IllegalStateException("optimistic lock conflict for user " + message.getUid());
        }

        PointsTransactionDO txn = new PointsTransactionDO();
        // Truncate UUID to fit ptid VARCHAR(32): "PT-" + 8 chars = 11 chars total.
        txn.setPtid("PT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        txn.setUid(message.getUid());
        txn.setRelatedOrderId(message.getRelatedOrderId());
        txn.setPointsChange(message.getPointsChange());
        txn.setType(message.getType());
        txn.setGmtCreate(now);
        txn.setGmtModified(now);
        transactionMapper.insert(txn);

        log.info("points adjusted: uid={}, change={}, newBalance={}",
                message.getUid(), message.getPointsChange(), newPoint);
    }
}