package com.firstclub.firstclub_membership.Scheduler;

import com.firstclub.firstclub_membership.Enums.SubscriptionStatus;
import com.firstclub.firstclub_membership.Models.UserMembership;
import com.firstclub.firstclub_membership.Repository.UserMembershipRepository;
import com.firstclub.firstclub_membership.Service.MembershipService;
import com.firstclub.firstclub_membership.Service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;

@Component
public class MembershipScheduler {
    private static final Logger logger = LoggerFactory.getLogger(MembershipScheduler.class);

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private UserMembershipRepository membershipRepository;

    @Autowired
    private UserService userService;

    @Scheduled(cron = "0 0 0 * * *")
    public void expireEndedMemberships() {
        logger.info("Scheduler: checking for expired memberships");
        membershipService.expireEndedMemberships();
    }

    @Scheduled(cron = "0 0 1 1 * *")
    public void resetMonthlyOrderValues() {
        logger.info("Scheduler: resetting monthly order values");
        userService.resetMonthlyOrderValues();
    }


    @Scheduled(fixedRate = 21600000)
    public void autoEvaluateTiersForAllMembers() {
        logger.info("Scheduler: auto-evaluating tiers for active members");

        List<UserMembership> activeMembers = membershipRepository.findByStatusAndEndDate(
                SubscriptionStatus.ACTIVE, LocalDate.now().plusDays(1)); // Sanity buffer

        membershipRepository.findAll().stream()
                .filter(m -> m.getStatus() == SubscriptionStatus.ACTIVE)
                .forEach(m -> {
                    try {
                        boolean upgraded = membershipService.autoEvaluateTier(m.getUser().getUserId());
                        if (upgraded) {
                            logger.info("Scheduler: auto-upgraded tier for user {}", m.getUser().getUserId());
                        }
                    } catch (Exception e) {
                        logger.error("Error evaluating tier for user {}: {}", m.getUser().getUserId(), e.getMessage());
                    }
                });
    }
}
