package com.hotel.hotel_loyalty.controller;

import com.hotel.hotel_loyalty.service.PointsRuleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * UC4 page controller — renders the admin points-rule JSP.
 * First screen is JSTL server-rendered; create/update via fetch (JSP+AJAX).
 *
 * @author wukewei
 * @since 1.0
 */
@Controller
@RequestMapping("/admin/rules")
public class AdminPointsRuleController {

    private final PointsRuleService pointsRuleService;

    public AdminPointsRuleController(PointsRuleService pointsRuleService) {
        this.pointsRuleService = pointsRuleService;
    }

    @GetMapping
    public String adminRules(Model model) {
        model.addAttribute("rules", pointsRuleService.listRules());
        return "admin_points_rules";
    }
}