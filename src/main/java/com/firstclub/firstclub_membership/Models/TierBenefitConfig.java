package com.firstclub.firstclub_membership.Models;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tier_benefit_configs",
        uniqueConstraints = @UniqueConstraint(columnNames = "tier"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TierBenefitConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer configId;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private MembershipTier tier;

    private Boolean freeDelivery;
    private Double freeDeliveryMinOrderValue; // Min order value to qualify for free delivery

    private Double extraDiscountPercent;

    private Boolean exclusiveDealsAccess;

    private Integer earlyAccessHours;

    private Boolean prioritySupport;

    private Integer exclusiveCouponsPerMonth;

    private Double maxDiscountCap;

    private String benefitSummary;
}
