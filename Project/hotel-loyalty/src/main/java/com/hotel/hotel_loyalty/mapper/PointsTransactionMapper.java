package com.hotel.hotel_loyalty.mapper;

import com.hotel.hotel_loyalty.domain.PointsTransactionDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * MyBatis mapper for the "points_transaction" table.
 * Insert-only from the redemption flow — transactions are an audit log, never updated or deleted.
 *
 * @author wukewei
 * @since 1.0
 */
@Mapper
public interface PointsTransactionMapper {

    @Insert("INSERT INTO points_transaction (ptid, uid, related_order_id, points_change, type, "
            + "gmt_create, gmt_modified) "
            + "VALUES (#{ptid}, #{uid}, #{relatedOrderId}, #{pointsChange}, #{type}, "
            + "#{gmtCreate}, #{gmtModified})")
    int insert(PointsTransactionDO transaction);

    @Select("SELECT id, ptid, uid, related_order_id, points_change, type, gmt_create, gmt_modified "
            + "FROM points_transaction WHERE uid = #{uid} ORDER BY gmt_create DESC")
    List<PointsTransactionDO> selectByUid(@Param("uid") String uid);

    @Select("SELECT id, ptid, uid, related_order_id, points_change, type, gmt_create, gmt_modified "
            + "FROM points_transaction WHERE related_order_id = #{oid} ORDER BY gmt_create DESC")
    List<PointsTransactionDO> selectByRelatedOrderId(@Param("oid") String oid);
}