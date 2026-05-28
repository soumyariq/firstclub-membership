package com.firstclub.firstclub_membership.Responses;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import com.firstclub.firstclub_membership.Enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AuditLogResponse {
    private Long logId;
    private Integer membershipId;
    private String action;
    private MembershipTier previousTier;
    private MembershipTier newTier;
    private SubscriptionStatus previousStatus;
    private SubscriptionStatus newStatus;
    private String remarks;
    private LocalDateTime performedAt;
}
