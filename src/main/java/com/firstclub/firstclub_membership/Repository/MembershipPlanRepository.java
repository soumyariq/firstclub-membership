package com.firstclub.firstclub_membership.Repository;

import com.firstclub.firstclub_membership.Enums.PlanDuration;
import com.firstclub.firstclub_membership.Models.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Integer> {
    Optional<MembershipPlan> findByDuration(PlanDuration duration);
    List<MembershipPlan> findByIsActiveTrue();
}
