package com.hotel.hotel_loyalty.mapper;

import com.hotel.hotel_loyalty.domain.PointsRuleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * MyBatis mapper for the "points_rule" table.
 * Read-only — rules are seeded via data.sql and managed by Manager/DBA.
 *
 * @author Fordring
 * @since 1.0
 */
@Mapper
public interface PointsRuleMapper {

    @Select("SELECT id, prid, name, type, threshold, points_ratio, gmt_create, gmt_modified "
            + "FROM points_rule WHERE type = #{type} AND threshold = #{threshold}")
    PointsRuleDO selectByTypeAndThreshold(@Param("type") Integer type,
                                          @Param("threshold") Integer threshold);

    @Select("SELECT id, prid, name, type, threshold, points_ratio, gmt_create, gmt_modified "
            + "FROM points_rule ORDER BY type, threshold")
    List<PointsRuleDO> selectAll();
}