package com.hotel.hotel_loyalty.service;

import com.hotel.hotel_loyalty.dto.PointsRuleDTO;
import com.hotel.hotel_loyalty.vo.RuleAdminVO;

import java.util.List;

/**
 * UC4 Manage Points Rules — service layer.
 * <p>
 * Flow (use case steps 1-5): Manager enters rules page → adds or modifies a
 * rule (type, threshold, ratio, description) → system validates and stores.
 *
 * @author wukewei
 * @since 1.0
 */
public interface PointsRuleService {

    /** UC4 step 1: list all rules for the admin table. */
    List<RuleAdminVO> listRules();

    /** UC4 step 2 (Read): get one rule by business key. */
    RuleAdminVO getRuleByPrid(String prid);

    /**
     * UC4 step 2-3 (Create): validate and save a new rule.
     *
     * @return generated prid
     */
    String createRule(PointsRuleDTO dto);

    /**
     * UC4 step 2-3 (Update): validate and save an existing rule.
     *
     * @param prid rule business key
     * @param dto  updated fields
     */
    void updateRule(String prid, PointsRuleDTO dto);
}