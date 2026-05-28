package com.firstclub.firstclub_membership.Models;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import com.firstclub.firstclub_membership.Enums.TierCriteriaType;
import com.firstclub.firstclub_membership.Enums.UserCohort;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tier_upgrade_criteria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TierUpgradeCriteria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer criteriaId;

    @Enumerated(EnumType.STRING)
    private MembershipTier targetTier;

    @Enumerated(EnumType.STRING)
    private TierCriteriaType criteriaType;

    private Integer minOrderCount;

    private Double minMonthlyOrderValue;

    @Enumerated(EnumType.STRING)
    private UserCohort requiredCohort;

    private Boolean isActive;

    @PrePersist
    @PreUpdate
    protected void defaults() {
        if (isActive == null) isActive = true;
    }
}
