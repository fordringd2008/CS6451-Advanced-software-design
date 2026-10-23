package com.hotel.hotel_loyalty.controller;

import com.hotel.hotel_loyalty.common.Result;
import com.hotel.hotel_loyalty.dto.PointsRuleDTO;
import com.hotel.hotel_loyalty.service.PointsRuleService;
import com.hotel.hotel_loyalty.vo.RuleAdminVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * UC4 Manage Points Rules — admin REST endpoints.
 * <p>
 * No DELETE endpoint: rules are never hard-deleted (audit trail preserved,
 * matching the GUI prototype which only shows an edit button).
 *
 * @author Fordring
 * @since 1.0
 */
@Tag(name = "UC4 积分规则", description = "Admin points rule management — use case 4")
@RestController
@RequestMapping("/api/admin/rules")
public class PointsRuleController {

    private final PointsRuleService pointsRuleService;

    public PointsRuleController(PointsRuleService pointsRuleService) {
        this.pointsRuleService = pointsRuleService;
    }

    @Operation(summary = "规则列表", description = "All rules for the admin table (GUI 积分规则 tab)")
    @GetMapping
    public Result<List<RuleAdminVO>> list() {
        return Result.success(pointsRuleService.listRules());
    }

    @Operation(summary = "规则详情", description = "Get one rule by business key")
    @GetMapping("/{prid}")
    public Result<RuleAdminVO> get(
            @Parameter(description = "Rule business key", example = "PR-L1")
            @PathVariable String prid) {
        return Result.success(pointsRuleService.getRuleByPrid(prid));
    }

    @Operation(summary = "新增规则", description = "UC4 step 2-3 — validates type/threshold/ratio then saves")
    @PostMapping
    public Result<String> create(@Valid @RequestBody PointsRuleDTO dto) {
        return Result.success(pointsRuleService.createRule(dto));
    }

    @Operation(summary = "编辑规则", description = "UC4 step 2-3 — validates then updates an existing rule")
    @PutMapping("/{prid}")
    public Result<Void> update(
            @Parameter(description = "Rule business key", example = "PR-L1")
            @PathVariable String prid,
            @Valid @RequestBody PointsRuleDTO dto) {
        pointsRuleService.updateRule(prid, dto);
        return Result.success();
    }
}