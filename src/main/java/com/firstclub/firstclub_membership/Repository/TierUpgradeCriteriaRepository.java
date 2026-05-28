package com.firstclub.firstclub_membership.Repository;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import com.firstclub.firstclub_membership.Models.TierUpgradeCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TierUpgradeCriteriaRepository extends JpaRepository<TierUpgradeCriteria, Integer> {
    List<TierUpgradeCriteria> findByTargetTierAndIsActiveTrue(MembershipTier targetTier);
}
