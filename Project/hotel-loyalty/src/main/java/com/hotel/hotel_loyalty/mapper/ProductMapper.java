package com.hotel.hotel_loyalty.mapper;

import com.hotel.hotel_loyalty.domain.ProductDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * MyBatis mapper for the "product" table (UC6 Manage Products).
 *
 * @author wukewei
 * @since 1.0
 */
@Mapper
public interface ProductMapper {

    @Select("SELECT id, pid, name, image_url, total_points_required, min_points_required, "
            + "total_stock, remaining_stock, gmt_create, gmt_modified "
            + "FROM product WHERE pid = #{pid}")
    ProductDO selectByPid(@Param("pid") String pid);

    @Select("SELECT id, pid, name, image_url, total_points_required, min_points_required, "
            + "total_stock, remaining_stock, gmt_create, gmt_modified "
            + "FROM product ORDER BY id")
    List<ProductDO> selectAll();

    /** Products available for guest redemption (UC3 step 1-2: remainingStock > 0). */
    @Select("SELECT id, pid, name, image_url, total_points_required, min_points_required, "
            + "total_stock, remaining_stock, gmt_create, gmt_modified "
            + "FROM product WHERE remaining_stock > 0 ORDER BY id")
    List<ProductDO> selectAvailable();

    @Insert("INSERT INTO product (pid, name, image_url, total_points_required, min_points_required, "
            + "total_stock, remaining_stock, gmt_create, gmt_modified) "
            + "VALUES (#{pid}, #{name}, #{imageUrl}, #{totalPointsRequired}, #{minPointsRequired}, "
            + "#{totalStock}, #{remainingStock}, #{gmtCreate}, #{gmtModified})")
    int insert(ProductDO product);

    @Update("UPDATE product SET name = #{name}, image_url = #{imageUrl}, "
            + "total_points_required = #{totalPointsRequired}, min_points_required = #{minPointsRequired}, "
            + "total_stock = #{totalStock}, remaining_stock = #{remainingStock}, "
            + "gmt_modified = #{gmtModified} WHERE pid = #{pid}")
    int updateByPid(ProductDO product);

    /** Deduct stock with optimistic lock (UC3 updateRemainingStock). */
    @Update("UPDATE product SET remaining_stock = remaining_stock - #{quantity}, "
            + "gmt_modified = #{gmtModified} "
            + "WHERE pid = #{pid} AND remaining_stock >= #{quantity}")
    int deductStock(@Param("pid") String pid,
                    @Param("quantity") Integer quantity,
                    @Param("gmtModified") java.time.LocalDateTime gmtModified);

    @Delete("DELETE FROM product WHERE pid = #{pid}")
    int deleteByPid(@Param("pid") String pid);
}