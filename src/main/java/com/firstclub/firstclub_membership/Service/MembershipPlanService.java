package com.firstclub.firstclub_membership.Service;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import com.firstclub.firstclub_membership.Enums.PlanDuration;
import com.firstclub.firstclub_membership.Enums.TierCriteriaType;
import com.firstclub.firstclub_membership.Enums.UserCohort;
import com.firstclub.firstclub_membership.Exceptions.ResourceNotFoundException;
import com.firstclub.firstclub_membership.Models.MembershipPlan;
import com.firstclub.firstclub_membership.Models.TierBenefitConfig;
import com.firstclub.firstclub_membership.Models.TierUpgradeCriteria;
import com.firstclub.firstclub_membership.Repository.MembershipPlanRepository;
import com.firstclub.firstclub_membership.Repository.TierBenefitConfigRepository;
import com.firstclub.firstclub_membership.Repository.TierUpgradeCriteriaRepository;
import com.firstclub.firstclub_membership.Requests.UpdateTierBenefitConfigRequest;
import com.firstclub.firstclub_membership.Responses.MembershipPlanResponse;
import com.firstclub.firstclub_membership.Responses.TierBenefitResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import java.util.stream.Collectors;

@Service
public class MembershipPlanService {
    private static final Logger logger = LoggerFactory.getLogger(MembershipPlanService.class);

    @Autowired
    private MembershipPlanRepository planRepository;

    @Autowired
    private TierBenefitConfigRepository tierBenefitConfigRepository;

    @Autowired
    private TierUpgradeCriteriaRepository tierUpgradeCriteriaRepository;

    @PostConstruct
    public void seedDefaultData() {
        seedPlans();
        seedTierBenefits();
        seedTierUpgradeCriteria();
    }

    private void seedPlans() {
        if (planRepository.count() > 0) return;
        logger.info("Seeding default membership plans");

        planRepository.saveAll(List.of(
                MembershipPlan.builder().duration(PlanDuration.MONTHLY).price(199.0)
                        .description("Monthly plan - billed every 30 days").isActive(true).build(),
                MembershipPlan.builder().duration(PlanDuration.QUARTERLY).price(499.0)
                        .description("Quarterly plan - billed every 90 days, save 16%").isActive(true).build(),
                MembershipPlan.builder().duration(PlanDuration.YEARLY).price(1499.0)
                        .description("Yearly plan - billed annually, save 37%").isActive(true).build()
        ));
    }

    private void seedTierBenefits() {
        if (tierBenefitConfigRepository.count() > 0) return;
        logger.info("Seeding default tier benefit configurations");

        tierBenefitConfigRepository.saveAll(List.of(
                TierBenefitConfig.builder()
                        .tier(MembershipTier.SILVER)
                        .freeDelivery(true)
                        .freeDeliveryMinOrderValue(499.0)
                        .extraDiscountPercent(5.0)
                        .exclusiveDealsAccess(false)
                        .earlyAccessHours(0)
                        .prioritySupport(false)
                        .exclusiveCouponsPerMonth(1)
                        .maxDiscountCap(200.0)
                        .benefitSummary("Free delivery on orders above ₹499, 5% extra discount, 1 exclusive coupon/month")
                        .build(),
                TierBenefitConfig.builder()
                        .tier(MembershipTier.GOLD)
                        .freeDelivery(true)
                        .freeDeliveryMinOrderValue(299.0)
                        .extraDiscountPercent(10.0)
                        .exclusiveDealsAccess(true)
                        .earlyAccessHours(12)
                        .prioritySupport(false)
                        .exclusiveCouponsPerMonth(3)
                        .maxDiscountCap(500.0)
                        .benefitSummary("Free delivery on orders above ₹299, 10% extra discount, early access 12h before sales, 3 coupons/month")
                        .build(),
                TierBenefitConfig.builder()
                        .tier(MembershipTier.PLATINUM)
                        .freeDelivery(true)
                        .freeDeliveryMinOrderValue(0.0)
                        .extraDiscountPercent(15.0)
                        .exclusiveDealsAccess(true)
                        .earlyAccessHours(24)
                        .prioritySupport(true)
                        .exclusiveCouponsPerMonth(5)
                        .maxDiscountCap(1000.0)
                        .benefitSummary("FREE delivery on ALL orders, 15% extra discount, 24h early access, priority support, 5 coupons/month")
                        .build()
        ));
    }

    private void seedTierUpgradeCriteria() {
        if (tierUpgradeCriteriaRepository.count() > 0) return;
        logger.info("Seeding default tier upgrade criteria");

        tierUpgradeCriteriaRepository.saveAll(List.of(
                TierUpgradeCriteria.builder()
                        .targetTier(MembershipTier.GOLD)
                        .criteriaType(TierCriteriaType.ORDER_COUNT)
                        .minOrderCount(10)
                        .isActive(true).build(),
                TierUpgradeCriteria.builder()
                        .targetTier(MembershipTier.GOLD)
                        .criteriaType(TierCriteriaType.ORDER_VALUE)
                        .minMonthlyOrderValue(5000.0)
                        .isActive(true).build(),
                TierUpgradeCriteria.builder()
                        .targetTier(MembershipTier.PLATINUM)
                        .criteriaType(TierCriteriaType.ORDER_COUNT)
                        .minOrderCount(25)
                        .isActive(true).build(),
                TierUpgradeCriteria.builder()
                        .targetTier(MembershipTier.PLATINUM)
                        .criteriaType(TierCriteriaType.ORDER_VALUE)
                        .minMonthlyOrderValue(15000.0)
                        .isActive(true).build(),
                TierUpgradeCriteria.builder()
                        .targetTier(MembershipTier.GOLD)
                        .criteriaType(TierCriteriaType.USER_COHORT)
                        .requiredCohort(UserCohort.STUDENT)
                        .isActive(true).build(),
                TierUpgradeCriteria.builder()
                        .targetTier(MembershipTier.PLATINUM)
                        .criteriaType(TierCriteriaType.USER_COHORT)
                        .requiredCohort(UserCohort.VIP)
                        .isActive(true).build()
        ));
    }

    public List<MembershipPlanResponse> getAllActivePlansWithTiers() {
        List<MembershipPlan> plans = planRepository.findByIsActiveTrue();
        Map<MembershipTier, TierBenefitResponse> tierMap = getAllTierBenefitsAsMap();

        return plans.stream().map(plan -> MembershipPlanResponse.builder()
                .planId(plan.getPlanId())
                .duration(plan.getDuration())
                .price(plan.getPrice())
                .description(plan.getDescription())
                .tierBenefits(tierMap)
                .build()
        ).collect(Collectors.toList());
    }

    public Map<MembershipTier, TierBenefitResponse> getAllTierBenefitsAsMap() {
        return tierBenefitConfigRepository.findAll().stream()
                .collect(Collectors.toMap(
                        TierBenefitConfig::getTier,
                        this::toTierBenefitResponse
                ));
    }

    public TierBenefitConfig getTierBenefitConfig(MembershipTier tier) {
        return tierBenefitConfigRepository.findByTier(tier)
                .orElseThrow(() -> new ResourceNotFoundException("TierBenefitConfig", "tier", tier));
    }

    public TierBenefitConfig updateTierBenefitConfig(MembershipTier tier, UpdateTierBenefitConfigRequest req) {
        logger.info("Updating tier benefit config for: {}", tier);
        TierBenefitConfig config = getTierBenefitConfig(tier);

        if (req.getFreeDelivery() != null) config.setFreeDelivery(req.getFreeDelivery());
        if (req.getFreeDeliveryMinOrderValue() != null) config.setFreeDeliveryMinOrderValue(req.getFreeDeliveryMinOrderValue());
        if (req.getExtraDiscountPercent() != null) config.setExtraDiscountPercent(req.getExtraDiscountPercent());
        if (req.getExclusiveDealsAccess() != null) config.setExclusiveDealsAccess(req.getExclusiveDealsAccess());
        if (req.getEarlyAccessHours() != null) config.setEarlyAccessHours(req.getEarlyAccessHours());
        if (req.getPrioritySupport() != null) config.setPrioritySupport(req.getPrioritySupport());
        if (req.getExclusiveCouponsPerMonth() != null) config.setExclusiveCouponsPerMonth(req.getExclusiveCouponsPerMonth());
        if (req.getMaxDiscountCap() != null) config.setMaxDiscountCap(req.getMaxDiscountCap());
        if (req.getBenefitSummary() != null) config.setBenefitSummary(req.getBenefitSummary());

        return tierBenefitConfigRepository.save(config);
    }

    public List<TierUpgradeCriteria> getAllUpgradeCriteria() {
        return tierUpgradeCriteriaRepository.findAll();
    }

    public MembershipPlan getPlanByDuration(PlanDuration duration) {
        return planRepository.findByDuration(duration)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", "duration", duration));
    }

    public TierBenefitResponse toTierBenefitResponse(TierBenefitConfig c) {
        return TierBenefitResponse.builder()
                .tier(c.getTier())
                .freeDelivery(c.getFreeDelivery())
                .freeDeliveryMinOrderValue(c.getFreeDeliveryMinOrderValue())
                .extraDiscountPercent(c.getExtraDiscountPercent())
                .exclusiveDealsAccess(c.getExclusiveDealsAccess())
                .earlyAccessHours(c.getEarlyAccessHours())
                .prioritySupport(c.getPrioritySupport())
                .exclusiveCouponsPerMonth(c.getExclusiveCouponsPerMonth())
                .maxDiscountCap(c.getMaxDiscountCap())
                .benefitSummary(c.getBenefitSummary())
                .build();
    }
}
