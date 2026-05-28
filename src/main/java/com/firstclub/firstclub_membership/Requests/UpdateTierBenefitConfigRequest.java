package com.firstclub.firstclub_membership.Requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UpdateTierBenefitConfigRequest {
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
