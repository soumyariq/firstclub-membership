package com.firstclub.firstclub_membership.Service;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import com.firstclub.firstclub_membership.Models.TierUpgradeCriteria;
import com.firstclub.firstclub_membership.Models.User;
import com.firstclub.firstclub_membership.Repository.TierUpgradeCriteriaRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.slf4j.Logger;

@Service
public class TierEvaluationService {
    private static final Logger logger = LoggerFactory.getLogger(TierEvaluationService.class);

    @Autowired
    private TierUpgradeCriteriaRepository tierUpgradeCriteriaRepository;

    public MembershipTier evaluateBestTier(User user) {
        MembershipTier best = MembershipTier.SILVER;

        for (MembershipTier tier : new MembershipTier[]{MembershipTier.PLATINUM, MembershipTier.GOLD}) {
            if (qualifiesForTier(user, tier)) {
                best = tier;
                break;
            }
        }

        logger.info("User {} evaluated to tier: {}", user.getUserId(), best);
        return best;
    }

    public boolean qualifiesForTier(User user, MembershipTier targetTier) {
        List<TierUpgradeCriteria> criteriaList = tierUpgradeCriteriaRepository.findByTargetTierAndIsActiveTrue(targetTier);

        for (TierUpgradeCriteria criteria : criteriaList) {
            if (matchesCriteria(user, criteria)) {
                logger.info("User {} qualifies for {} via {} criteria",
                        user.getUserId(), targetTier, criteria.getCriteriaType());
                return true;
            }
        }
        return false;
    }

    private boolean matchesCriteria(User user, TierUpgradeCriteria criteria) {
        return switch (criteria.getCriteriaType()) {
            case ORDER_COUNT -> user.getTotalOrderCount() >= criteria.getMinOrderCount();
            case ORDER_VALUE -> user.getTotalOrderValueCurrentMonth() >= criteria.getMinMonthlyOrderValue();
            case USER_COHORT -> user.getCohort() == criteria.getRequiredCohort();
        };
    }
}
