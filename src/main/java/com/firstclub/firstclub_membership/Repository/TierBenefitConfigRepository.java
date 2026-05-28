package com.firstclub.firstclub_membership.Repository;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import com.firstclub.firstclub_membership.Models.TierBenefitConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TierBenefitConfigRepository extends JpaRepository<TierBenefitConfig, Integer> {
    Optional<TierBenefitConfig> findByTier(MembershipTier tier);
}
