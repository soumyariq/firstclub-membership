package com.firstclub.firstclub_membership.Models;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import com.firstclub.firstclub_membership.Enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "membership_audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MembershipAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne
    @JoinColumn(name = "membership_id")
    private UserMembership membership;

    private String action;

    @Enumerated(EnumType.STRING)
    private MembershipTier previousTier;

    @Enumerated(EnumType.STRING)
    private MembershipTier newTier;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus previousStatus;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus newStatus;

    private String remarks;
    private LocalDateTime performedAt;

    @PrePersist
    protected void onCreate() {
        performedAt = LocalDateTime.now();
    }
}
