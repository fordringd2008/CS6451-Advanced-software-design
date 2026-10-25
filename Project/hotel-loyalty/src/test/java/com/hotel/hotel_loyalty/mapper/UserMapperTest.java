package com.hotel.hotel_loyalty.mapper;

import com.hotel.hotel_loyalty.domain.UserDO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Slice tests for {@link UserMapper} — real H2, real SQL.
 * Focus: CAS optimistic lock on point deduction.
 *
 * @author Fordring
 * @since 1.0
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    private LocalDateTime now = LocalDateTime.of(2026, 10, 25, 0, 0, 0);

    /** Insert a user with the given point balance. */
    private void insertUser(String uid, int currentPoint) {
        UserDO user = new UserDO();
        user.setUid(uid);
        user.setName("test user " + uid);
        user.setPassword("$2a$testhash");
        user.setStatus(0);
        user.setRole(0);
        user.setRegisterTime(now);
        user.setLevel(1);
        user.setCurrentPoint(currentPoint);
        user.setGmtCreate(now);
        user.setGmtModified(now);
        userMapper.insert(user);
    }

    @Test
    void testSelectByUid_whenUserExists_shouldReturn() {
        // given
        insertUser("test_u_001", 200);
        // when
        UserDO found = userMapper.selectByUid("test_u_001");
        // then
        assertThat(found).isNotNull();
        assertThat(found.getCurrentPoint()).isEqualTo(200);
    }

    @Test
    void testCompareAndUpdatePoint_whenExpectedOldPointMatches_shouldReturnOneAndUpdate() {
        // given: user has 200 points; CAS expects old = 200, new = 150
        insertUser("test_u_010", 200);
        // when
        int affected = userMapper.compareAndUpdatePoint("test_u_010", 200, 150, now);
        // then
        assertThat(affected).isEqualTo(1);
        assertThat(userMapper.selectByUid("test_u_010").getCurrentPoint()).isEqualTo(150);
    }

    @Test
    void testCompareAndUpdatePoint_whenExpectedOldPointMismatch_shouldReturnZeroAndNotUpdate() {
        // given: user has 200 points; CAS expects old = 999 (already changed by another thread)
        insertUser("test_u_020", 200);
        // when
        int affected = userMapper.compareAndUpdatePoint("test_u_020", 999, 150, now);
        // then: CAS fails, balance unchanged
        assertThat(affected).isZero();
        assertThat(userMapper.selectByUid("test_u_020").getCurrentPoint()).isEqualTo(200);
    }

    @Test
    void testInsert_shouldPersistUser() {
        // given
        insertUser("test_u_030", 500);
        // when
        UserDO found = userMapper.selectByUid("test_u_030");
        // then
        assertThat(found).isNotNull();
        assertThat(found.getUid()).isEqualTo("test_u_030");
        assertThat(found.getLevel()).isEqualTo(1);
    }
}