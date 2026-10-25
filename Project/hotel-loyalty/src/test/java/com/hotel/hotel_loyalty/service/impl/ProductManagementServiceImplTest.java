package com.hotel.hotel_loyalty.service.impl;

import com.hotel.hotel_loyalty.domain.ProductDO;
import com.hotel.hotel_loyalty.dto.ProductCreateDTO;
import com.hotel.hotel_loyalty.dto.ProductUpdateDTO;
import com.hotel.hotel_loyalty.mapper.OrderMapper;
import com.hotel.hotel_loyalty.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pure unit tests for {@link ProductManagementServiceImpl} — UC6 business rules.
 * No Spring context: Mapper dependencies are Mockito mocks.
 *
 * @author Fordring
 * @since 1.0
 */
class ProductManagementServiceImplTest {

    private final ProductMapper productMapper = mock(ProductMapper.class);
    private final OrderMapper orderMapper = mock(OrderMapper.class);
    private final ProductManagementServiceImpl service =
            new ProductManagementServiceImpl(productMapper, orderMapper);

    /** Build a valid UC6 create form (min=1000 <= total=2000, stock=20). */
    private ProductCreateDTO validCreateDTO() {
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName("Spa Voucher");
        dto.setImageUrl("http://test/spa.png");
        dto.setTotalPointsRequired(2000);
        dto.setMinPointsRequired(1000);
        dto.setTotalStock(20);
        return dto;
    }

    @Test
    void testCreateProduct_whenValid_shouldSetRemainingStockEqualToTotalStock() {
        // given
        when(productMapper.insert(any())).thenReturn(1);
        // when
        String pid = service.createProduct(validCreateDTO());
        // then: UC6 rule — on create remainingStock = totalStock
        assertThat(pid).startsWith("P-");
        ArgumentCaptor<ProductDO> captor = ArgumentCaptor.forClass(ProductDO.class);
        verify(productMapper).insert(captor.capture());
        assertThat(captor.getValue().getRemainingStock()).isEqualTo(20);
        assertThat(captor.getValue().getTotalStock()).isEqualTo(20);
    }

    @Test
    void testCreateProduct_whenMinPointsExceedsTotalPoints_shouldThrow() {
        // given: UC6 Extension 3a — min > total
        ProductCreateDTO dto = validCreateDTO();
        dto.setMinPointsRequired(3000);
        dto.setTotalPointsRequired(2000);
        // when + then
        assertThatThrownBy(() -> service.createProduct(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot exceed");
        verify(productMapper, never()).insert(any());
    }

    @Test
    void testUpdateProduct_whenRemainingStockExceedsTotalStock_shouldThrow() {
        // given: UC6 Extension 5a — remaining > total
        ProductUpdateDTO dto = new ProductUpdateDTO();
        dto.setPid("P-001");
        dto.setName("Updated");
        dto.setImageUrl("http://test/upd.png");
        dto.setTotalPointsRequired(2000);
        dto.setMinPointsRequired(1000);
        dto.setTotalStock(10);
        dto.setRemainingStock(15);
        // when + then
        assertThatThrownBy(() -> service.updateProduct(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot exceed");
        verify(productMapper, never()).updateByPid(any());
    }

    @Test
    void testDeleteProduct_whenActiveOrdersExist_shouldThrow() {
        // given: UC6 Extension 6a — pending/paid orders reference the product
        when(productMapper.selectByPid("P-001")).thenReturn(new ProductDO());
        when(orderMapper.countActiveByPid("P-001")).thenReturn(2);
        // when + then
        assertThatThrownBy(() -> service.deleteProduct("P-001"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("active order");
        verify(productMapper, never()).deleteByPid(anyString());
    }

    @Test
    void testDeleteProduct_whenNoActiveOrders_shouldDelete() {
        // given
        when(productMapper.selectByPid("P-001")).thenReturn(new ProductDO());
        when(orderMapper.countActiveByPid("P-001")).thenReturn(0);
        // when
        service.deleteProduct("P-001");
        // then
        verify(productMapper).deleteByPid("P-001");
    }

    @Test
    void testGetProductByPid_whenNotFound_shouldThrow() {
        // given
        when(productMapper.selectByPid("P-999")).thenReturn(null);
        // when + then
        assertThatThrownBy(() -> service.getProductByPid("P-999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}