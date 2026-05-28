package com.firstclub.firstclub_membership.Responses;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import com.firstclub.firstclub_membership.Enums.PlanDuration;
import com.firstclub.firstclub_membership.Enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class MembershipResponse {
    private Integer membershipId;
    private Integer userId;
    private String userName;
    private String userEmail;
    private PlanDuration planDuration;
    private Double planPrice;
    private MembershipTier tier;
    private SubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double amountPaid;
    private LocalDateTime subscribedAt;
    private LocalDateTime cancelledAt;
    private Long daysRemaining;
    private TierBenefitResponse currentBenefits;
}
