package com.hotel.hotel_loyalty.mapper;

import com.hotel.hotel_loyalty.domain.ProductDO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Slice tests for {@link ProductMapper} — real H2 in-memory DB, real SQL.
 * Each test inserts its own data (test_ prefix); @MybatisTest auto-rollbacks.
 *
 * @author Fordring
 * @since 1.0
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    private LocalDateTime now = LocalDateTime.of(2026, 10, 25, 0, 0, 0);

    /** Insert a product with the given remaining stock. */
    private ProductDO insertProduct(String pid, String name, int remainingStock) {
        ProductDO product = new ProductDO();
        product.setPid(pid);
        product.setName(name);
        product.setImageUrl("http://test/" + pid + ".png");
        product.setTotalPointsRequired(100);
        product.setMinPointsRequired(50);
        product.setTotalStock(10);
        product.setRemainingStock(remainingStock);
        product.setGmtCreate(now);
        product.setGmtModified(now);
        productMapper.insert(product);
        return product;
    }

    @Test
    void testSelectByPid_whenProductExists_shouldReturn() {
        // given
        insertProduct("test_p_001", "test product 1", 5);
        // when
        ProductDO found = productMapper.selectByPid("test_p_001");
        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("test product 1");
        assertThat(found.getRemainingStock()).isEqualTo(5);
    }

    @Test
    void testSelectByPid_whenProductNotExists_shouldReturnNull() {
        // given: no product with this pid
        // when
        ProductDO found = productMapper.selectByPid("test_p_nonexistent");
        // then
        assertThat(found).isNull();
    }

    @Test
    void testSelectAll_shouldReturnAllProductsIncludingSoldOut() {
        // given: one in-stock, one sold-out
        insertProduct("test_p_010", "in stock", 5);
        insertProduct("test_p_011", "sold out", 0);
        // when: admin sees everything
        List<ProductDO> all = productMapper.selectAll();
        // then
        assertThat(all).extracting(ProductDO::getPid)
                .contains("test_p_010", "test_p_011");
    }

    @Test
    void testSelectAvailable_shouldExcludeSoldOutProducts() {
        // given: one in-stock, one sold-out
        insertProduct("test_p_020", "in stock", 5);
        insertProduct("test_p_021", "sold out", 0);
        // when: guest mall only shows available products
        List<ProductDO> available = productMapper.selectAvailable();
        // then: sold-out must not appear (UC3 step 1-2 alignment)
        assertThat(available).extracting(ProductDO::getPid)
                .contains("test_p_020")
                .doesNotContain("test_p_021");
    }

    @Test
    void testDeductStock_whenStockSufficient_shouldReturnOneAndDeduct() {
        // given
        insertProduct("test_p_030", "stock test", 5);
        // when
        int affected = productMapper.deductStock("test_p_030", 3, now);
        // then
        assertThat(affected).isEqualTo(1);
        assertThat(productMapper.selectByPid("test_p_030").getRemainingStock()).isEqualTo(2);
    }

    @Test
    void testDeductStock_whenStockInsufficient_shouldReturnZeroAndNotDeduct() {
        // given: only 2 in stock, try to deduct 3
        insertProduct("test_p_040", "stock test", 2);
        // when
        int affected = productMapper.deductStock("test_p_040", 3, now);
        // then: CAS fails, stock unchanged
        assertThat(affected).isZero();
        assertThat(productMapper.selectByPid("test_p_040").getRemainingStock()).isEqualTo(2);
    }
}