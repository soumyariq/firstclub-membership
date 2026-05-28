package com.firstclub.firstclub_membership.Service;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import com.firstclub.firstclub_membership.Enums.PlanDuration;
import com.firstclub.firstclub_membership.Enums.SubscriptionStatus;
import com.firstclub.firstclub_membership.Exceptions.BusinessException;
import com.firstclub.firstclub_membership.Exceptions.MembershipException;
import com.firstclub.firstclub_membership.Exceptions.ResourceNotFoundException;
import com.firstclub.firstclub_membership.Models.*;
import com.firstclub.firstclub_membership.Repository.MembershipAuditLogRepository;
import com.firstclub.firstclub_membership.Repository.UserMembershipRepository;
import com.firstclub.firstclub_membership.Requests.CancelMembershipRequest;
import com.firstclub.firstclub_membership.Requests.ChangeTierRequest;
import com.firstclub.firstclub_membership.Requests.SubscribeRequest;
import com.firstclub.firstclub_membership.Responses.AuditLogResponse;
import com.firstclub.firstclub_membership.Responses.MembershipResponse;
import com.firstclub.firstclub_membership.Responses.TierBenefitResponse;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;

@Service
public class MembershipService {
    private static final Logger logger = LoggerFactory.getLogger(MembershipService.class);

    @Autowired
    private UserMembershipRepository membershipRepository;

    @Autowired
    private MembershipAuditLogRepository auditLogRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MembershipPlanService planService;

    @Autowired
    private TierEvaluationService tierEvaluationService;

    @Transactional
    public MembershipResponse subscribe(SubscribeRequest req) {
        logger.info("Subscribing user {} to plan {} tier {}", req.getUserId(), req.getPlanDuration(), req.getTier());

        User user = userService.getUserById(req.getUserId());

        boolean alreadyActive = membershipRepository.existsByUserAndStatus(user, SubscriptionStatus.ACTIVE);
        if (alreadyActive) {
            throw new MembershipException("User already has an active membership. Cancel or modify the existing one.");
        }

        MembershipPlan plan = planService.getPlanByDuration(req.getPlanDuration());

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = computeEndDate(startDate, req.getPlanDuration());

        MembershipTier resolvedTier = resolveTier(user, req.getTier());

        UserMembership membership = UserMembership.builder()
                .user(user)
                .plan(plan)
                .tier(resolvedTier)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(startDate)
                .endDate(endDate)
                .amountPaid(plan.getPrice())
                .build();

        membership = membershipRepository.save(membership);

        saveAuditLog(membership, "SUBSCRIBED", null, null, SubscriptionStatus.PENDING, SubscriptionStatus.ACTIVE,
                "New subscription: plan=" + req.getPlanDuration() + ", tier=" + resolvedTier);

        logger.info("Membership {} created for user {}", membership.getMembershipId(), user.getUserId());
        return toResponse(membership);
    }

    @Transactional
    public MembershipResponse changeTier(Integer userId, ChangeTierRequest req) {
        logger.info("Changing tier for user {} to {}", userId, req.getNewTier());

        User user = userService.getUserById(userId);
        UserMembership membership = membershipRepository.findActiveByUserWithLock(user)
                .orElseThrow(() -> new ResourceNotFoundException("No active membership found for user: " + userId));
        MembershipTier oldTier = membership.getTier();
        MembershipTier newTier = req.getNewTier();

        if (oldTier == newTier) {
            throw new BusinessException("User is already on tier: " + newTier);
        }
        String action = tierRank(newTier) > tierRank(oldTier) ? "UPGRADED" : "DOWNGRADED";
        membership.setTier(newTier);
        membership = membershipRepository.save(membership);

        saveAuditLog(membership, action, oldTier, newTier, SubscriptionStatus.ACTIVE, SubscriptionStatus.ACTIVE,
                req.getReason() != null ? req.getReason() : action + " by user request");

        logger.info("Membership {} tier changed from {} to {}", membership.getMembershipId(), oldTier, newTier);
        return toResponse(membership);
    }
    @Transactional
    public MembershipResponse cancelMembership(Integer userId, CancelMembershipRequest req) {
        logger.info("Cancelling membership for user {}", userId);

        User user = userService.getUserById(userId);

        UserMembership membership = membershipRepository.findActiveByUserWithLock(user)
                .orElseThrow(() -> new ResourceNotFoundException("No active membership found for user: " + userId));

        MembershipTier oldTier = membership.getTier();
        membership.setStatus(SubscriptionStatus.CANCELLED);
        membership.setCancelledAt(LocalDateTime.now());
        membership.setCancellationReason(req.getReason());
        membership = membershipRepository.save(membership);

        saveAuditLog(membership, "CANCELLED", oldTier, oldTier, SubscriptionStatus.ACTIVE, SubscriptionStatus.CANCELLED,
                req.getReason() != null ? req.getReason() : "User requested cancellation");

        logger.info("Membership {} cancelled for user {}", membership.getMembershipId(), userId);
        return toResponse(membership);
    }

    public MembershipResponse getMembership(Integer userId) {
        User user = userService.getUserById(userId);
        UserMembership membership = membershipRepository.findByUserAndStatus(user, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active membership found for user: " + userId));
        return toResponse(membership);
    }

    public List<MembershipResponse> getMembershipHistory(Integer userId) {
        User user = userService.getUserById(userId);
        return membershipRepository.findByUserOrderBySubscribedAtDesc(user)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<AuditLogResponse> getAuditLog(Integer membershipId) {
        UserMembership m = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership", "membershipId", membershipId));
        return auditLogRepository.findByMembershipOrderByPerformedAtDesc(m)
                .stream()
                .map(log -> AuditLogResponse.builder()
                        .logId(log.getLogId())
                        .membershipId(log.getMembership().getMembershipId())
                        .action(log.getAction())
                        .previousTier(log.getPreviousTier())
                        .newTier(log.getNewTier())
                        .previousStatus(log.getPreviousStatus())
                        .newStatus(log.getNewStatus())
                        .remarks(log.getRemarks())
                        .performedAt(log.getPerformedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean autoEvaluateTier(Integer userId) {
        User user = userService.getUserById(userId);
        UserMembership membership = membershipRepository.findByUserAndStatus(user, SubscriptionStatus.ACTIVE)
                .orElse(null);

        if (membership == null) return false;

        MembershipTier currentTier = membership.getTier();
        MembershipTier bestTier = tierEvaluationService.evaluateBestTier(user);

        if (tierRank(bestTier) > tierRank(currentTier)) {
            MembershipTier oldTier = membership.getTier();
            membership.setTier(bestTier);
            membershipRepository.save(membership);

            saveAuditLog(membership, "AUTO_TIER_UPGRADE", oldTier, bestTier,
                    SubscriptionStatus.ACTIVE, SubscriptionStatus.ACTIVE,
                    "Automatic tier upgrade based on order activity");

            logger.info("Auto-upgraded user {} from {} to {}", userId, oldTier, bestTier);
            return true;
        }
        return false;
    }

    @Transactional
    public void expireEndedMemberships() {
        List<UserMembership> expired = membershipRepository
                .findByStatusAndEndDate(SubscriptionStatus.ACTIVE, LocalDate.now().minusDays(1));
        expired.forEach(m -> {
            MembershipTier tier = m.getTier();
            m.setStatus(SubscriptionStatus.EXPIRED);
            membershipRepository.save(m);
            saveAuditLog(m, "EXPIRED", tier, tier, SubscriptionStatus.ACTIVE, SubscriptionStatus.EXPIRED,
                    "Membership expired on end date");
            logger.info("Membership {} expired", m.getMembershipId());
        });
    }

    private LocalDate computeEndDate(LocalDate start, PlanDuration duration) {
        return switch (duration) {
            case MONTHLY -> start.plusMonths(1);
            case QUARTERLY -> start.plusMonths(3);
            case YEARLY -> start.plusYears(1);
        };
    }

    private MembershipTier resolveTier(User user, MembershipTier requested) {
        if (requested == MembershipTier.SILVER) return MembershipTier.SILVER;

        if (tierEvaluationService.qualifiesForTier(user, requested)) {
            return requested;
        }

        if (requested == MembershipTier.PLATINUM && tierEvaluationService.qualifiesForTier(user, MembershipTier.GOLD)) {
            logger.warn("User does not qualify for PLATINUM, assigning GOLD");
            return MembershipTier.GOLD;
        }
        logger.warn("User does not qualify for {}, assigning SILVER", requested);
        return MembershipTier.SILVER;
    }

    private int tierRank(MembershipTier tier) {
        return switch (tier) {
            case SILVER -> 1;
            case GOLD -> 2;
            case PLATINUM -> 3;
        };
    }

    private void saveAuditLog(UserMembership m, String action,
                              MembershipTier prevTier, MembershipTier newTier,
                              SubscriptionStatus prevStatus, SubscriptionStatus newStatus,
                              String remarks) {
        auditLogRepository.save(MembershipAuditLog.builder()
                .membership(m)
                .action(action)
                .previousTier(prevTier)
                .newTier(newTier)
                .previousStatus(prevStatus)
                .newStatus(newStatus)
                .remarks(remarks)
                .build());
    }

    public MembershipResponse toResponse(UserMembership m) {
        TierBenefitConfig config = planService.getTierBenefitConfig(m.getTier());
        TierBenefitResponse benefits = planService.toTierBenefitResponse(config);

        long daysRemaining = 0;
        if (m.getStatus() == SubscriptionStatus.ACTIVE && m.getEndDate() != null) {
            daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), m.getEndDate());
            if (daysRemaining < 0) daysRemaining = 0;
        }

        return MembershipResponse.builder()
                .membershipId(m.getMembershipId())
                .userId(m.getUser().getUserId())
                .userName(m.getUser().getName())
                .userEmail(m.getUser().getEmail())
                .planDuration(m.getPlan().getDuration())
                .planPrice(m.getPlan().getPrice())
                .tier(m.getTier())
                .status(m.getStatus())
                .startDate(m.getStartDate())
                .endDate(m.getEndDate())
                .amountPaid(m.getAmountPaid())
                .subscribedAt(m.getSubscribedAt())
                .cancelledAt(m.getCancelledAt())
                .daysRemaining(daysRemaining)
                .currentBenefits(benefits)
                .build();
    }
}