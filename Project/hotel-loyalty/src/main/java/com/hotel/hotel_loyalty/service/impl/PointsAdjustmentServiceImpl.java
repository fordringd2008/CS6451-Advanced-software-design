package com.hotel.hotel_loyalty.service.impl;

import com.hotel.hotel_loyalty.domain.UserDO;
import com.hotel.hotel_loyalty.dto.PointsAdjustmentDTO;
import com.hotel.hotel_loyalty.enums.UserStatusEnum;
import com.hotel.hotel_loyalty.mapper.UserMapper;
import com.hotel.hotel_loyalty.messaging.PointsAdjustmentProducer;
import com.hotel.hotel_loyalty.service.PointsAdjustmentService;
import com.hotel.hotel_loyalty.vo.UserPointsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * UC5 Manually Add Points — implementation.
 * <p>
 * Synchronous part (this class): verify user (precondition 2) → enqueue.
 * Asynchronous part ({@code PointsAdjustmentConsumer}): credit points +
 * create PointsTransaction, with automatic retry on failure.
 * <p>
 * Note: the optional reason (use case step 2) is recorded in the application
 * log for audit. It is not persisted to points_transaction because the table
 * has no reason column (schema.sql) — adding one would be a guess.
 *
 * @author Fordring
 * @since 1.0
 */
@Service
public class PointsAdjustmentServiceImpl implements PointsAdjustmentService {

    private static final Logger log = LoggerFactory.getLogger(PointsAdjustmentServiceImpl.class);

    private final UserMapper userMapper;
    private final PointsAdjustmentProducer producer;

    public PointsAdjustmentServiceImpl(UserMapper userMapper, PointsAdjustmentProducer producer) {
        this.userMapper = userMapper;
        this.producer = producer;
    }

    @Override
    public UserPointsVO getUserByUid(String uid) {
        UserDO user = userMapper.selectByUid(uid);
        if (user == null) {
            throw new IllegalArgumentException("user not found: " + uid);
        }
        return toVO(user);
    }

    @Override
    public String adjustPoints(PointsAdjustmentDTO dto) {
        UserDO user = userMapper.selectByUid(dto.getUid());
        if (user == null) {
            throw new IllegalArgumentException("user not found: " + dto.getUid());
        }

        // Precondition 2: target user account status is normal.
        UserStatusEnum status = UserStatusEnum.fromCode(user.getStatus());
        if (status != UserStatusEnum.NORMAL) {
            throw new IllegalStateException(
                    "cannot adjust points for user " + dto.getUid()
                            + ": account status is " + (status != null ? status.getDescription() : "unknown"));
        }

        // UC5 step 4: place the request into the persistent queue.
        // Type is EARN — this use case only adds points (use case title: "Manually Add Points").
        producer.send(dto.getUid(), dto.getPoints(), "EARN", null);

        // Audit log for the optional reason (not persisted — no reason column in schema).
        log.info("points adjustment enqueued: uid={}, points={}, reason={}",
                dto.getUid(), dto.getPoints(),
                (dto.getReason() == null || dto.getReason().isBlank()) ? "(none)" : dto.getReason());

        return "Accepted, entered processing queue";
    }

    private UserPointsVO toVO(UserDO user) {
        UserPointsVO vo = new UserPointsVO();
        vo.setUid(user.getUid());
        vo.setName(user.getName());
        vo.setStatus(user.getStatus());
        vo.setLevel(user.getLevel());
        vo.setCurrentPoint(user.getCurrentPoint());
        return vo;
    }
}