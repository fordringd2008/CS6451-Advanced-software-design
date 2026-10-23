package com.hotel.hotel_loyalty.service.impl;

import com.hotel.hotel_loyalty.domain.ProductDO;
import com.hotel.hotel_loyalty.dto.ProductCreateDTO;
import com.hotel.hotel_loyalty.dto.ProductUpdateDTO;
import com.hotel.hotel_loyalty.mapper.OrderMapper;
import com.hotel.hotel_loyalty.mapper.ProductMapper;
import com.hotel.hotel_loyalty.service.ProductManagementService;
import com.hotel.hotel_loyalty.vo.ProductAdminVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * UC6 implementation — all business rules from the use-case description
 * and sequence diagram, layered per the architecture document (service
 * layer between web and dao).
 * <p>
 * Class-level @Transactional: deleteProduct is a check-then-act pattern
 * (count active orders → delete) that must be atomic — without a transaction
 * a concurrent order could slip in between the check and the delete.
 *
 * @author Fordring
 * @since 1.0
 */
@Service
@Transactional
public class ProductManagementServiceImpl implements ProductManagementService {

    private static final Logger log = LoggerFactory.getLogger(ProductManagementServiceImpl.class);

    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    public ProductManagementServiceImpl(ProductMapper productMapper, OrderMapper orderMapper) {
        this.productMapper = productMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public List<ProductAdminVO> listProducts() {
        return productMapper.selectAll().stream()
                .map(this::toAdminVO)
                .toList();
    }

    @Override
    public ProductAdminVO getProductByPid(String pid) {
        ProductDO product = productMapper.selectByPid(pid);
        if (product == null) {
            throw new IllegalArgumentException("product not found: " + pid);
        }
        return toAdminVO(product);
    }

    @Override
    public String createProduct(ProductCreateDTO dto) {
        validateCreate(dto);

        LocalDateTime now = LocalDateTime.now();
        ProductDO product = new ProductDO();
        product.setPid(generatePid());
        product.setName(dto.getName());
        product.setImageUrl(dto.getImageUrl());
        product.setTotalPointsRequired(dto.getTotalPointsRequired());
        product.setMinPointsRequired(dto.getMinPointsRequired());
        product.setTotalStock(dto.getTotalStock());
        // UC6: on create, remainingStock = totalStock
        product.setRemainingStock(dto.getTotalStock());
        product.setGmtCreate(now);
        product.setGmtModified(now);

        productMapper.insert(product);
        log.info("product created: pid={}, name={}", product.getPid(), product.getName());
        return product.getPid();
    }

    @Override
    public void updateProduct(ProductUpdateDTO dto) {
        validateUpdate(dto);

        ProductDO existing = productMapper.selectByPid(dto.getPid());
        if (existing == null) {
            throw new IllegalArgumentException("product not found: " + dto.getPid());
        }

        ProductDO product = new ProductDO();
        product.setPid(dto.getPid());
        product.setName(dto.getName());
        product.setImageUrl(dto.getImageUrl());
        product.setTotalPointsRequired(dto.getTotalPointsRequired());
        product.setMinPointsRequired(dto.getMinPointsRequired());
        product.setTotalStock(dto.getTotalStock());
        product.setRemainingStock(dto.getRemainingStock());
        product.setGmtModified(LocalDateTime.now());

        productMapper.updateByPid(product);
        log.info("product updated: pid={}", dto.getPid());
    }

    @Override
    public void deleteProduct(String pid) {
        ProductDO existing = productMapper.selectByPid(pid);
        if (existing == null) {
            throw new IllegalArgumentException("product not found: " + pid);
        }
        // UC6 Extension 6a: block delete if pending/paid orders exist
        int activeOrders = orderMapper.countActiveByPid(pid);
        if (activeOrders > 0) {
            throw new IllegalStateException(
                    "cannot delete product " + pid + ": " + activeOrders + " active order(s) reference it");
        }
        productMapper.deleteByPid(pid);
        log.info("product deleted: pid={}", pid);
    }

    // ---------- validation (UC6 Extensions 3a / 5a) ----------

    private void validateCreate(ProductCreateDTO dto) {
        if (dto.getMinPointsRequired() > dto.getTotalPointsRequired()) {
            throw new IllegalArgumentException(
                    "min points required (" + dto.getMinPointsRequired()
                            + ") cannot exceed total points required (" + dto.getTotalPointsRequired() + ")");
        }
    }

    private void validateUpdate(ProductUpdateDTO dto) {
        if (dto.getMinPointsRequired() > dto.getTotalPointsRequired()) {
            throw new IllegalArgumentException(
                    "min points required (" + dto.getMinPointsRequired()
                            + ") cannot exceed total points required (" + dto.getTotalPointsRequired() + ")");
        }
        if (dto.getRemainingStock() > dto.getTotalStock()) {
            throw new IllegalArgumentException(
                    "remaining stock (" + dto.getRemainingStock()
                            + ") cannot exceed total stock (" + dto.getTotalStock() + ")");
        }
    }

    // ---------- helpers ----------

    private String generatePid() {
        return "P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ProductAdminVO toAdminVO(ProductDO product) {
        ProductAdminVO vo = new ProductAdminVO();
        vo.setPid(product.getPid());
        vo.setName(product.getName());
        vo.setImageUrl(product.getImageUrl());
        vo.setTotalPointsRequired(product.getTotalPointsRequired());
        vo.setMinPointsRequired(product.getMinPointsRequired());
        vo.setRemainingStock(product.getRemainingStock());
        vo.setTotalStock(product.getTotalStock());
        return vo;
    }
}