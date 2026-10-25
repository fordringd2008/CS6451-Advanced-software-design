package com.hotel.hotel_loyalty.messaging;

import com.hotel.hotel_loyalty.domain.PointsTransactionDO;
import com.hotel.hotel_loyalty.domain.UserDO;
import com.hotel.hotel_loyalty.mapper.PointsTransactionMapper;
import com.hotel.hotel_loyalty.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pure unit tests for {@link PointsAdjustmentConsumer} (UC5 async part).
 * <p>
 * Covers the four outcomes of consuming a points-adjustment message:
 * success (CAS update + transaction insert), unknown user, negative
 * balance guard, and optimistic-lock conflict (triggers redelivery).
 *
 * @author Fordring
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class PointsAdjustmentConsumerTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PointsTransactionMapper transactionMapper;

    @InjectMocks
    private PointsAdjustmentConsumer consumer;

    @Test
    @DisplayName("valid message: CAS update succeeds and a transaction record is inserted")
    void testOnMessage_whenValidMessage_shouldCreditPointsAndInsertTransaction() {
        // given: guest has 5000 points; CAS update will succeed
        UserDO user = new UserDO();
        user.setUid("guest");
        user.setCurrentPoint(5000);
        when(userMapper.selectByUid("guest")).thenReturn(user);
        when(userMapper.compareAndUpdatePoint(eq("guest"), eq(5000), eq(5200), any()))
                .thenReturn(1);
        // when: consumer processes an EARN message of +200
        consumer.onMessage(new PointsAdjustmentMessage("guest", 200, "EARN", null));
        // then: CAS update called and an audit transaction with all fields is inserted
        ArgumentCaptor<PointsTransactionDO> captor = ArgumentCaptor.forClass(PointsTransactionDO.class);
        verify(transactionMapper).insert(captor.capture());
        PointsTransactionDO txn = captor.getValue();
        assertThat(txn.getUid()).isEqualTo("guest");
        assertThat(txn.getPointsChange()).isEqualTo(200);
        assertThat(txn.getType()).isEqualTo("EARN");
        assertThat(txn.getRelatedOrderId()).isNull();
        assertThat(txn.getPtid()).startsWith("PT-");
    }

    @Test
    @DisplayName("unknown user: message processing fails (triggers Artemis redelivery)")
    void testOnMessage_whenUserNotFound_shouldThrowIllegalState() {
        // given: no user exists for the message uid
        when(userMapper.selectByUid("nobody")).thenReturn(null);
        // when + then: consumer throws, no points or transaction written
        assertThatThrownBy(() -> consumer.onMessage(
                new PointsAdjustmentMessage("nobody", 100, "EARN", null)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("user not found");
        verify(userMapper, never()).compareAndUpdatePoint(anyString(), anyInt(), anyInt(), any());
        verify(transactionMapper, never()).insert(any());
    }

    @Test
    @DisplayName("negative balance guard: redeem more than balance is rejected")
    void testOnMessage_whenPointsWouldGoNegative_shouldThrowIllegalState() {
        // given: guest only has 100 points
        UserDO user = new UserDO();
        user.setUid("guest");
        user.setCurrentPoint(100);
        when(userMapper.selectByUid("guest")).thenReturn(user);
        // when + then: REDEEM of -200 would go negative; consumer throws
        assertThatThrownBy(() -> consumer.onMessage(
                new PointsAdjustmentMessage("guest", -200, "REDEEM", "O-1")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("negative");
        verify(userMapper, never()).compareAndUpdatePoint(anyString(), anyInt(), anyInt(), any());
        verify(transactionMapper, never()).insert(any());
    }

    @Test
    @DisplayName("optimistic-lock conflict: CAS returns 0, exception forces redelivery")
    void testOnMessage_whenOptimisticLockConflict_shouldThrowIllegalState() {
        // given: CAS update returns 0 (concurrent modification)
        UserDO user = new UserDO();
        user.setUid("guest");
        user.setCurrentPoint(5000);
        when(userMapper.selectByUid("guest")).thenReturn(user);
        when(userMapper.compareAndUpdatePoint(eq("guest"), eq(5000), eq(5100), any()))
                .thenReturn(0);
        // when + then: consumer throws to force Artemis redelivery
        assertThatThrownBy(() -> consumer.onMessage(
                new PointsAdjustmentMessage("guest", 100, "EARN", null)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("optimistic lock conflict");
        verify(transactionMapper, never()).insert(any());
    }
}