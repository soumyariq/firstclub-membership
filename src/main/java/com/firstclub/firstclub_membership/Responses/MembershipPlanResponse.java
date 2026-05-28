package com.firstclub.firstclub_membership.Responses;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import com.firstclub.firstclub_membership.Enums.PlanDuration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class MembershipPlanResponse {
    private Integer planId;
    private PlanDuration duration;
    private Double price;
    private String description;
    private Map<MembershipTier, TierBenefitResponse> tierBenefits;
}
