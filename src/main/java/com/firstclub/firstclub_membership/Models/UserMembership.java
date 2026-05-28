package com.firstclub.firstclub_membership.Models;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import com.firstclub.firstclub_membership.Enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_memberships")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer membershipId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private MembershipPlan plan;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MembershipTier tier = MembershipTier.SILVER;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    private LocalDate startDate;
    private LocalDate endDate;

    private Double amountPaid;

    private LocalDateTime subscribedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime lastModifiedAt;

    private String cancellationReason;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        subscribedAt = LocalDateTime.now();
        lastModifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedAt = LocalDateTime.now();
    }
}
