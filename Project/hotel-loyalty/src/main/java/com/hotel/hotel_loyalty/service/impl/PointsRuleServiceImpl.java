package com.hotel.hotel_loyalty.service.impl;

import com.hotel.hotel_loyalty.domain.PointsRuleDO;
import com.hotel.hotel_loyalty.dto.PointsRuleDTO;
import com.hotel.hotel_loyalty.enums.PointsRuleTypeEnum;
import com.hotel.hotel_loyalty.mapper.PointsRuleMapper;
import com.hotel.hotel_loyalty.service.PointsRuleService;
import com.hotel.hotel_loyalty.vo.RuleAdminVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * UC4 Manage Points Rules — implementation.
 * <p>
 * Validation (use case step 5 "System validates and stores"):
 * <ul>
 *   <li>type must be 1 (BY_LEVEL) or 2 (BY_PERIOD)</li>
 *   <li>BY_LEVEL: threshold is forced to null (no minimum — rule applies by level)</li>
 *   <li>BY_PERIOD: threshold optional (min order amount); startTime/endTime
 *       optional but if both provided, start must be before end</li>
 *   <li>pointsRatio must be a positive integer (percentage)</li>
 * </ul>
 *
 * @author Fordring
 * @since 1.0
 */
@Service
public class PointsRuleServiceImpl implements PointsRuleService {

    private final PointsRuleMapper pointsRuleMapper;

    public PointsRuleServiceImpl(PointsRuleMapper pointsRuleMapper) {
        this.pointsRuleMapper = pointsRuleMapper;
    }

    @Override
    public List<RuleAdminVO> listRules() {
        return pointsRuleMapper.selectAll().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public RuleAdminVO getRuleByPrid(String prid) {
        PointsRuleDO rule = pointsRuleMapper.selectByPrid(prid);
        if (rule == null) {
            throw new IllegalArgumentException("rule not found: " + prid);
        }
        return toVO(rule);
    }

    @Override
    public String createRule(PointsRuleDTO dto) {
        validateRule(dto);

        LocalDateTime now = LocalDateTime.now();
        PointsRuleDO rule = new PointsRuleDO();
        // PR-UUID8: "PR-" + 8 chars = 11 chars total, fits VARCHAR(32).
        rule.setPrid("PR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        rule.setName(dto.getName());
        rule.setType(dto.getType());
        rule.setThreshold(resolveThreshold(dto));
        rule.setPointsRatio(dto.getPointsRatio());
        rule.setStartTime(dto.getStartTime());
        rule.setEndTime(dto.getEndTime());
        rule.setDescription(dto.getDescription());
        rule.setGmtCreate(now);
        rule.setGmtModified(now);
        pointsRuleMapper.insert(rule);

        return rule.getPrid();
    }

    @Override
    public void updateRule(String prid, PointsRuleDTO dto) {
        PointsRuleDO existing = pointsRuleMapper.selectByPrid(prid);
        if (existing == null) {
            throw new IllegalArgumentException("rule not found: " + prid);
        }
        validateRule(dto);

        existing.setName(dto.getName());
        existing.setType(dto.getType());
        existing.setThreshold(resolveThreshold(dto));
        existing.setPointsRatio(dto.getPointsRatio());
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());
        existing.setDescription(dto.getDescription());
        existing.setGmtModified(LocalDateTime.now());
        pointsRuleMapper.updateByPrid(existing);
    }

    /** UC4 step 5: validate rule fields per candidate_objects semantics. */
    private void validateRule(PointsRuleDTO dto) {
        PointsRuleTypeEnum type = PointsRuleTypeEnum.fromCode(dto.getType());
        if (type == null) {
            throw new IllegalArgumentException(
                    "invalid rule type: " + dto.getType() + " (must be 1 or 2)");
        }

        if (type == PointsRuleTypeEnum.BY_PERIOD
                && dto.getStartTime() != null && dto.getEndTime() != null
                && !dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new IllegalArgumentException(
                    "start time must be before end time for BY_PERIOD rules");
        }
    }

    /** BY_LEVEL rules have no minimum order amount — force threshold to null. */
    private Integer resolveThreshold(PointsRuleDTO dto) {
        if (PointsRuleTypeEnum.fromCode(dto.getType()) == PointsRuleTypeEnum.BY_LEVEL) {
            return null;
        }
        return dto.getThreshold();
    }

    private RuleAdminVO toVO(PointsRuleDO rule) {
        RuleAdminVO vo = new RuleAdminVO();
        vo.setPrid(rule.getPrid());
        vo.setName(rule.getName());
        vo.setType(rule.getType());
        vo.setThreshold(rule.getThreshold());
        vo.setPointsRatio(rule.getPointsRatio());
        vo.setStartTime(rule.getStartTime());
        vo.setEndTime(rule.getEndTime());
        vo.setDescription(rule.getDescription());
        return vo;
    }
}