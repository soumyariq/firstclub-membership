package com.firstclub.firstclub_membership.Models;

import com.firstclub.firstclub_membership.Enums.PlanDuration;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "membership_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MembershipPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer planId;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private PlanDuration duration;

    private Double price;
    private String description;
    private Boolean isActive;

    @PrePersist
    @PreUpdate
    protected void defaults() {
        if (isActive == null) isActive = true;
    }
}
