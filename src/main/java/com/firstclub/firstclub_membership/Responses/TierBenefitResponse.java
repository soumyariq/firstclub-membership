package com.firstclub.firstclub_membership.Responses;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TierBenefitResponse {
    private MembershipTier tier;
    private Boolean freeDelivery;
    private Double freeDeliveryMinOrderValue;
    private Double extraDiscountPercent;
    private Boolean exclusiveDealsAccess;
    private Integer earlyAccessHours;
    private Boolean prioritySupport;
    private Integer exclusiveCouponsPerMonth;
    private Double maxDiscountCap;
    private String benefitSummary;
}
