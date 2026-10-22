package com.hotel.hotel_loyalty.mapper;

import com.hotel.hotel_loyalty.domain.OrderDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * MyBatis mapper for the "order" table.
 *
 * @author Fordring
 * @since 1.0
 */
@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO \"order\" (oid, uid, pid, pmid, points_used, cash_used, voucher_used, "
            + "status, gmt_create, gmt_modified) "
            + "VALUES (#{oid}, #{uid}, #{pid}, #{pmid}, #{pointsUsed}, #{cashUsed}, #{voucherUsed}, "
            + "#{status}, #{gmtCreate}, #{gmtModified})")
    int insert(OrderDO order);

    @Select("SELECT id, oid, uid, pid, pmid, points_used, cash_used, voucher_used, "
            + "status, gmt_create, gmt_modified "
            + "FROM \"order\" WHERE oid = #{oid}")
    OrderDO selectByOid(@Param("oid") String oid);

    @Select("SELECT id, oid, uid, pid, pmid, points_used, cash_used, voucher_used, "
            + "status, gmt_create, gmt_modified "
            + "FROM \"order\" WHERE uid = #{uid} ORDER BY gmt_create DESC")
    List<OrderDO> selectByUid(@Param("uid") String uid);

    /**
     * UC6 submitDelete precondition: count orders for a product that are not yet finalised.
     * Status codes are hardcoded (annotation values must be compile-time constants):
     * 1 = PENDING_PAYMENT, 2 = PAID (see OrderStatusEnum).
     */
    @Select("SELECT COUNT(*) FROM \"order\" WHERE pid = #{pid} AND status IN (1, 2)")
    int countActiveByPid(@Param("pid") String pid);

    /** State transition with guard: only succeeds if current status matches expected (state machine). */
    @Update("UPDATE \"order\" SET status = #{newStatus}, gmt_modified = #{gmtModified} "
            + "WHERE oid = #{oid} AND status = #{expectedOldStatus}")
    int compareAndUpdateStatus(@Param("oid") String oid,
                               @Param("expectedOldStatus") Integer expectedOldStatus,
                               @Param("newStatus") Integer newStatus,
                               @Param("gmtModified") java.time.LocalDateTime gmtModified);
}