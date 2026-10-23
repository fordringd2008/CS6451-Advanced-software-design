package com.hotel.hotel_loyalty.controller;

import com.hotel.hotel_loyalty.common.Result;
import com.hotel.hotel_loyalty.dto.ProductCreateDTO;
import com.hotel.hotel_loyalty.dto.ProductUpdateDTO;
import com.hotel.hotel_loyalty.service.ProductManagementService;
import com.hotel.hotel_loyalty.vo.ProductAdminVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * UC6 Manage Products — admin REST endpoints.
 * <p>
 * Layered per the architecture document: Controller (web) → Service → Mapper (dao).
 * Every method returns the unified {@link Result} wrapper (Alibaba rule).
 *
 * @author Fordring
 * @since 1.0
 */
@Tag(name = "UC6 Product Management", description = "Admin product CRUD — use case 6")
@RestController
@RequestMapping("/api/admin/products")
public class ProductManagementController {

    private final ProductManagementService productManagementService;

    public ProductManagementController(ProductManagementService productManagementService) {
        this.productManagementService = productManagementService;
    }

    @Operation(summary = "Product list", description = "All products for the admin table (GUI product-management tab)")
    @GetMapping
    public Result<List<ProductAdminVO>> list() {
        return Result.success(productManagementService.listProducts());
    }

    @Operation(summary = "Product detail", description = "Get one product by business key")
    @GetMapping("/{pid}")
    public Result<ProductAdminVO> get(
            @Parameter(description = "Product business key", example = "P-001")
            @PathVariable String pid) {
        return Result.success(productManagementService.getProductByPid(pid));
    }

    @Operation(summary = "Create product", description = "UC6 submitCreate — remainingStock = totalStock on create")
    @PostMapping
    public Result<String> create(@Valid @RequestBody ProductCreateDTO dto) {
        return Result.success(productManagementService.createProduct(dto));
    }

    @Operation(summary = "Edit product", description = "UC6 submitUpdate — validates min ≤ total, remaining ≤ total")
    @PutMapping("/{pid}")
    public Result<Void> update(
            @Parameter(description = "Product business key", example = "P-001")
            @PathVariable String pid,
            @Valid @RequestBody ProductUpdateDTO dto) {
        dto.setPid(pid);
        productManagementService.updateProduct(dto);
        return Result.success();
    }

    @Operation(summary = "Delete product",
            description = "UC6 submitDelete — Extension 6a: blocked (409) if pending/paid orders reference it")
    @DeleteMapping("/{pid}")
    public Result<Void> delete(
            @Parameter(description = "Product business key", example = "P-001")
            @PathVariable String pid) {
        productManagementService.deleteProduct(pid);
        return Result.success();
    }
}