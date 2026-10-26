package com.hotel.hotel_loyalty.service;

import com.hotel.hotel_loyalty.dto.ProductCreateDTO;
import com.hotel.hotel_loyalty.dto.ProductUpdateDTO;
import com.hotel.hotel_loyalty.vo.ProductAdminVO;

import java.util.List;

/**
 * UC6 Manage Products — full CRUD for points-mall products.
 * <p>
 * Flow (UC6 sequence diagram + use-case description):
 * <ul>
 *   <li>create: validate → save (remainingStock = totalStock)</li>
 *   <li>update: validateUpdate → update</li>
 *   <li>delete: checkPendingOrders → delete (block if active orders exist)</li>
 *   <li>list / get: read queries</li>
 * </ul>
 *
 * @author wukewei
 * @since 1.0
 */
public interface ProductManagementService {

    /** List all products for the admin table. */
    List<ProductAdminVO> listProducts();

    /** Get one product by business key. */
    ProductAdminVO getProductByPid(String pid);

    /**
     * Create a new product (UC6 submitCreate).
     *
     * @return generated pid
     */
    String createProduct(ProductCreateDTO dto);

    /** Update an existing product (UC6 submitUpdate). */
    void updateProduct(ProductUpdateDTO dto);

    /**
     * Delete a product (UC6 submitDelete).
     *
     * @throws IllegalStateException if the product has pending/paid orders (UC6 Extension 6a)
     */
    void deleteProduct(String pid);
}