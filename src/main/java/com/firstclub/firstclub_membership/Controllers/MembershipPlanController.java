package com.firstclub.firstclub_membership.Controllers;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import com.firstclub.firstclub_membership.Models.TierBenefitConfig;
import com.firstclub.firstclub_membership.Models.TierUpgradeCriteria;
import com.firstclub.firstclub_membership.Requests.UpdateTierBenefitConfigRequest;
import com.firstclub.firstclub_membership.Responses.ApiResponse;
import com.firstclub.firstclub_membership.Responses.MembershipPlanResponse;
import com.firstclub.firstclub_membership.Responses.TierBenefitResponse;
import com.firstclub.firstclub_membership.Service.MembershipPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api/v1/plans")
@Tag(name = "Membership Plans & Tiers", description = "APIs to view plans, tier benefits, and upgrade criteria")
public class MembershipPlanController {
    private static final Logger logger = LoggerFactory.getLogger(MembershipPlanController.class);

    @Autowired
    private MembershipPlanService planService;

    @GetMapping
    @Operation(summary = "Get all active plans with tier benefits",
            description = "Returns all available plans (Monthly/Quarterly/Yearly) with full tier benefit details for each tier")
    public ResponseEntity<ApiResponse<List<MembershipPlanResponse>>> getAllPlans() {
        logger.info("Fetching all active membership plans");
        List<MembershipPlanResponse> plans = planService.getAllActivePlansWithTiers();
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    @GetMapping("/tiers")
    @Operation(summary = "Get all tier benefits",
            description = "Returns benefit configuration for all tiers (Silver, Gold, Platinum)")
    public ResponseEntity<ApiResponse<Map<MembershipTier, TierBenefitResponse>>> getAllTierBenefits() {
        logger.info("Fetching all tier benefit configs");
        return ResponseEntity.ok(ApiResponse.success(planService.getAllTierBenefitsAsMap()));
    }

    @GetMapping("/tiers/{tier}")
    @Operation(summary = "Get benefits for a specific tier")
    public ResponseEntity<ApiResponse<TierBenefitResponse>> getTierBenefits(@PathVariable MembershipTier tier) {
        logger.info("Fetching tier benefits for: {}", tier);
        TierBenefitConfig config = planService.getTierBenefitConfig(tier);
        return ResponseEntity.ok(ApiResponse.success(planService.toTierBenefitResponse(config)));
    }

    @PutMapping("/tiers/{tier}")
    @Operation(summary = "Update tier benefit config",
            description = "Admin API to reconfigure benefits for a tier (fully configurable)")
    public ResponseEntity<ApiResponse<TierBenefitConfig>> updateTierBenefits(
            @PathVariable MembershipTier tier,
            @RequestBody UpdateTierBenefitConfigRequest req) {
        logger.info("Updating tier benefit config for: {}", tier);
        TierBenefitConfig updated = planService.updateTierBenefitConfig(tier, req);
        return ResponseEntity.ok(ApiResponse.success("Tier benefit config updated", updated));
    }

    @GetMapping("/upgrade-criteria")
    @Operation(summary = "Get all tier upgrade criteria",
            description = "Returns all configured rules for automatic tier upgrades")
    public ResponseEntity<ApiResponse<List<TierUpgradeCriteria>>> getUpgradeCriteria() {
        logger.info("Fetching tier upgrade criteria");
        return ResponseEntity.ok(ApiResponse.success(planService.getAllUpgradeCriteria()));
    }
}
