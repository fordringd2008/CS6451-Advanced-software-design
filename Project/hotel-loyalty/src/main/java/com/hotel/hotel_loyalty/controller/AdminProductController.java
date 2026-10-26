package com.hotel.hotel_loyalty.controller;

import com.hotel.hotel_loyalty.service.ProductManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * UC6 Manage Products — admin page controller.
 * <p>
 * Renders {@code admin_products.jsp} with the product list via JSTL.
 * Create / update / delete are performed by the page's JavaScript through
 * {@link ProductManagementController} REST endpoints ({@code /api/admin/products}),
 * so this controller only handles the initial GET (MVC pattern: Controller →
 * Service → DAO, JSP view as presentation layer).
 * <p>
 * View resolution: {@code spring.mvc.view.prefix=/WEB-INF/jsp/} + {@code suffix=.jsp},
 * so returning {@code "admin_products"} resolves to {@code /WEB-INF/jsp/admin_products.jsp}.
 *
 * @author wukewei
 * @since 1.0
 */
@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductManagementService productManagementService;

    public AdminProductController(ProductManagementService productManagementService) {
        this.productManagementService = productManagementService;
    }

    /** Admin product-management page rendered by JSP + JSTL. */
    @GetMapping
    public String adminProducts(Model model) {
        model.addAttribute("products", productManagementService.listProducts());
        return "admin_products";
    }
}