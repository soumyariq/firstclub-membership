package com.firstclub.firstclub_membership.Repository;

import com.firstclub.firstclub_membership.Enums.SubscriptionStatus;
import com.firstclub.firstclub_membership.Models.User;
import com.firstclub.firstclub_membership.Models.UserMembership;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMembershipRepository extends JpaRepository<UserMembership, Integer> {
    Optional<UserMembership> findByUserAndStatus(User user, SubscriptionStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM UserMembership m WHERE m.user = :user AND m.status = 'ACTIVE'")
    Optional<UserMembership> findActiveByUserWithLock(@Param("user") User user);

    List<UserMembership> findByUserOrderBySubscribedAtDesc(User user);
    List<UserMembership> findByStatusAndEndDate(SubscriptionStatus status, LocalDate endDate);

    boolean existsByUserAndStatus(User user, SubscriptionStatus status);
}
