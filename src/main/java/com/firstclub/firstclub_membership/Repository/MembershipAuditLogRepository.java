package com.firstclub.firstclub_membership.Repository;

import com.firstclub.firstclub_membership.Models.MembershipAuditLog;
import com.firstclub.firstclub_membership.Models.UserMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipAuditLogRepository extends JpaRepository<MembershipAuditLog, Long> {
    List<MembershipAuditLog> findByMembershipOrderByPerformedAtDesc(UserMembership membership);
}
