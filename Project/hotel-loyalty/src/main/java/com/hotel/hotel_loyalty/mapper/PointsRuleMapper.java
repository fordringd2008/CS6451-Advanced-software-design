package com.hotel.hotel_loyalty.mapper;

import com.hotel.hotel_loyalty.domain.PointsRuleDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * MyBatis mapper for the "points_rule" table.
 * UC4 Manage Points Rules — full CRUD (create, read, update; rules are never
 * hard-deleted to preserve audit trail, but update can disable via end_time).
 *
 * @author wukewei
 * @since 1.0
 */
@Mapper
public interface PointsRuleMapper {

    String COLS = "id, prid, name, type, threshold, points_ratio, start_time, end_time, description, gmt_create, gmt_modified ";

    @Select("SELECT " + COLS + "FROM points_rule WHERE prid = #{prid}")
    PointsRuleDO selectByPrid(@Param("prid") String prid);

    @Select("SELECT " + COLS + "FROM points_rule WHERE type = #{type} AND threshold = #{threshold}")
    PointsRuleDO selectByTypeAndThreshold(@Param("type") Integer type,
                                          @Param("threshold") Integer threshold);

    @Select("SELECT " + COLS + "FROM points_rule ORDER BY type, prid")
    List<PointsRuleDO> selectAll();

    @Insert("INSERT INTO points_rule (prid, name, type, threshold, points_ratio, start_time, end_time, description, "
            + "gmt_create, gmt_modified) "
            + "VALUES (#{prid}, #{name}, #{type}, #{threshold}, #{pointsRatio}, #{startTime}, #{endTime}, "
            + "#{description}, #{gmtCreate}, #{gmtModified})")
    int insert(PointsRuleDO rule);

    @Update("UPDATE points_rule SET name = #{name}, type = #{type}, threshold = #{threshold}, "
            + "points_ratio = #{pointsRatio}, start_time = #{startTime}, end_time = #{endTime}, "
            + "description = #{description}, gmt_modified = #{gmtModified} "
            + "WHERE prid = #{prid}")
    int updateByPrid(PointsRuleDO rule);
}