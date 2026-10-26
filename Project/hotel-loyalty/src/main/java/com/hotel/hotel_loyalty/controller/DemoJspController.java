package com.hotel.hotel_loyalty.controller;

import com.hotel.hotel_loyalty.service.ProductManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * JSP demo — renders the points-mall product list through a server-side view
 * (MVC pattern per the architecture document: Controller → Service → DAO,
 * with a JSP view as the presentation layer).
 * <p>
 * View resolution: {@code spring.mvc.view.prefix=/WEB-INF/jsp/} + {@code suffix=.jsp},
 * so returning {@code "products"} resolves to {@code /WEB-INF/jsp/products.jsp}.
 *
 * @author wukewei
 * @since 1.0
 */
@Controller
@RequestMapping("/demo")
public class DemoJspController {

    private final ProductManagementService productManagementService;

    public DemoJspController(ProductManagementService productManagementService) {
        this.productManagementService = productManagementService;
    }

    /** Product list page rendered by JSP + JSTL. */
    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productManagementService.listProducts());
        return "products";
    }
}