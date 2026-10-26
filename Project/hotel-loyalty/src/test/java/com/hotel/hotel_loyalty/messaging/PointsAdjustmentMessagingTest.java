package com.hotel.hotel_loyalty.messaging;

import com.hotel.hotel_loyalty.domain.PointsTransactionDO;
import com.hotel.hotel_loyalty.domain.ProductDO;
import com.hotel.hotel_loyalty.domain.UserDO;
import com.hotel.hotel_loyalty.dto.PointsAdjustmentDTO;
import com.hotel.hotel_loyalty.mapper.PointsTransactionMapper;
import com.hotel.hotel_loyalty.mapper.ProductMapper;
import com.hotel.hotel_loyalty.mapper.UserMapper;
import com.hotel.hotel_loyalty.service.PointsAdjustmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration tests for the UC5 asynchronous points-adjustment pipeline
 * (Producer → embedded Artemis queue → Consumer → DB) and the in-process
 * reward-distribution event (architecture document 5.0 — Event-Driven).
 * <p>
 * Uses the real embedded broker (spring-boot-starter-artemis-test) and the
 * real {@code @JmsListener} container — no mocks. {@code @DirtiesContext}
 * rebuilds the context after each test so the credited points never leak
 * into other test classes sharing the same H2 database.
 *
 * @author Fordring
 * @since 1.0
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PointsAdjustmentMessagingTest {

    /** Seed balance of the demo guest in data.sql. */
    private static final int GUEST_SEED_POINTS = 5000;

    @Autowired
    private PointsAdjustmentProducer producer;

    @Autowired
    private PointsAdjustmentService pointsAdjustmentService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PointsTransactionMapper transactionMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("producer.send → consumer credits points and all message fields are preserved")
    void testSend_whenValidMessage_shouldCreditPointsAndPreserveFields() {
        // given: guest has 5000 seed points; message carries all four fields
        // when: producer enqueues an EARN adjustment with a related order id
        producer.send("guest", 200, "EARN", "O-TEST01");

        // then 1: consumer credits the balance asynchronously (Awaitility poll)
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            UserDO user = userMapper.selectByUid("guest");
            assertThat(user.getCurrentPoint()).isEqualTo(GUEST_SEED_POINTS + 200);
        });
        // then 2: message body deserialized correctly — all fields preserved in the transaction
        PointsTransactionDO txn = transactionMapper.selectByUid("guest").get(0);
        assertThat(txn.getPointsChange()).isEqualTo(200);
        assertThat(txn.getType()).isEqualTo("EARN");
        assertThat(txn.getRelatedOrderId()).isEqualTo("O-TEST01");
    }

    @Test
    @DisplayName("PointsAdjustmentService.adjustPoints → enqueue → consumer credits end-to-end")
    void testAdjustPoints_whenValidDto_shouldEnqueueAndCreditAsynchronously() {
        // given: a valid UC5 adjustment DTO for the seed guest
        PointsAdjustmentDTO dto = new PointsAdjustmentDTO();
        dto.setUid("guest");
        dto.setPoints(300);
        dto.setReason("loyalty bonus");
        // when: service validates and enqueues
        String result = pointsAdjustmentService.adjustPoints(dto);
        // then 1: service returns the queue-accepted confirmation
        assertThat(result).isEqualTo("Accepted, entered processing queue");
        // then 2: consumer credits the balance asynchronously
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            UserDO user = userMapper.selectByUid("guest");
            assertThat(user.getCurrentPoint()).isEqualTo(GUEST_SEED_POINTS + 300);
        });
    }

    @Test
    @DisplayName("PointsDeductedEvent published → listener does NOT deduct stock (by design)")
    void testOnPointsDeducted_whenEventPublished_shouldNotDeductStock() {
        // Design decision: stock deduction stays inside the redemption
        // transaction (GuestPointsServiceImpl.redeem, explicit concurrency
        // control via deductStock). The listener is notification-only —
        // this test locks that decision so it is not changed by accident.
        // given: seed product P-003 has 200 in stock
        int stockBefore = productMapper.selectByPid("P-003").getRemainingStock();
        // when: a PointsDeductedEvent is published (in-process, synchronous)
        eventPublisher.publishEvent(new PointsDeductedEvent("O-1", "guest", "P-003", 800));
        // then: stock is unchanged — the listener only logs, it does not deduct
        ProductDO product = productMapper.selectByPid("P-003");
        assertThat(product.getRemainingStock()).isEqualTo(stockBefore);
    }
}