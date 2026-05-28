package com.firstclub.firstclub_membership.Requests;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import com.firstclub.firstclub_membership.Enums.PlanDuration;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SubscribeRequest {
    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Plan duration is required")
    private PlanDuration planDuration;

    @NotNull(message = "Membership tier is required")
    private MembershipTier tier;
}
