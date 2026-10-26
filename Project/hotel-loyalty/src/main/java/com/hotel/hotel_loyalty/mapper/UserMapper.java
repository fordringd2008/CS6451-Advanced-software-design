package com.hotel.hotel_loyalty.mapper;

import com.hotel.hotel_loyalty.domain.UserDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * MyBatis mapper for the "user" table.
 * Note: no SELECT * (Alibaba rule), all parameters use #{} (never ${}).
 *
 * @author wukewei
 * @since 1.0
 */
@Mapper
public interface UserMapper {

    @Select("SELECT id, uid, name, password, status, role, register_time, "
            + "level, current_point, gmt_create, gmt_modified "
            + "FROM \"user\" WHERE uid = #{uid}")
    UserDO selectByUid(@Param("uid") String uid);

    @Select("SELECT id, uid, name, password, status, role, register_time, "
            + "level, current_point, gmt_create, gmt_modified "
            + "FROM \"user\" ORDER BY id")
    List<UserDO> selectAll();

    @Insert("INSERT INTO \"user\" (uid, name, password, status, role, register_time, "
            + "level, current_point, gmt_create, gmt_modified) "
            + "VALUES (#{uid}, #{name}, #{password}, #{status}, #{role}, #{registerTime}, "
            + "#{level}, #{currentPoint}, #{gmtCreate}, #{gmtModified})")
    int insert(UserDO user);

    @Update("UPDATE \"user\" SET current_point = #{newPoint}, gmt_modified = #{gmtModified} "
            + "WHERE uid = #{uid} AND current_point = #{expectedOldPoint}")
    int compareAndUpdatePoint(@Param("uid") String uid,
                              @Param("expectedOldPoint") Integer expectedOldPoint,
                              @Param("newPoint") Integer newPoint,
                              @Param("gmtModified") java.time.LocalDateTime gmtModified);

    @Update("UPDATE \"user\" SET level = #{level}, gmt_modified = #{gmtModified} WHERE uid = #{uid}")
    int updateLevel(@Param("uid") String uid,
                    @Param("level") Integer level,
                    @Param("gmtModified") java.time.LocalDateTime gmtModified);

    @Update("UPDATE \"user\" SET status = #{status}, gmt_modified = #{gmtModified} WHERE uid = #{uid}")
    int updateStatus(@Param("uid") String uid,
                     @Param("status") Integer status,
                     @Param("gmtModified") java.time.LocalDateTime gmtModified);
}