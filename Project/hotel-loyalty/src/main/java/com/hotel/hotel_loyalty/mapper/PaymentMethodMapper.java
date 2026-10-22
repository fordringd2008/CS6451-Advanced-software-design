package com.hotel.hotel_loyalty.mapper;

import com.hotel.hotel_loyalty.domain.PaymentMethodDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * MyBatis mapper for the "payment_method" table.
 * Read-only — payment methods are seeded via data.sql and managed by DBA.
 *
 * @author Fordring
 * @since 1.0
 */
@Mapper
public interface PaymentMethodMapper {

    @Select("SELECT id, pmid, name, description, gmt_create, gmt_modified "
            + "FROM payment_method WHERE pmid = #{pmid}")
    PaymentMethodDO selectByPmid(@Param("pmid") String pmid);

    @Select("SELECT id, pmid, name, description, gmt_create, gmt_modified "
            + "FROM payment_method ORDER BY id")
    List<PaymentMethodDO> selectAll();
}