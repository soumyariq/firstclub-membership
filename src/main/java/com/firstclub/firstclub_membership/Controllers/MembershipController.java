package com.firstclub.firstclub_membership.Controllers;

import com.firstclub.firstclub_membership.Responses.AuditLogResponse;
import com.firstclub.firstclub_membership.Requests.CancelMembershipRequest;
import com.firstclub.firstclub_membership.Requests.ChangeTierRequest;
import com.firstclub.firstclub_membership.Requests.SubscribeRequest;
import com.firstclub.firstclub_membership.Responses.ApiResponse;
import com.firstclub.firstclub_membership.Responses.MembershipResponse;
import com.firstclub.firstclub_membership.Service.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;


@RestController
@RequestMapping("/api/v1/memberships")
@Tag(name = "Membership Subscriptions", description = "APIs for subscribing, modifying, and tracking memberships")
public class MembershipController {
    private static final Logger logger = LoggerFactory.getLogger(MembershipController.class);

    @Autowired
    private MembershipService membershipService;

    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe to a membership plan",
            description = "Subscribe user to a plan (Monthly/Quarterly/Yearly) with a chosen tier (Silver/Gold/Platinum). " +
                    "Tier will be auto-resolved based on eligibility criteria.")
    public ResponseEntity<ApiResponse<MembershipResponse>> subscribe(@Valid @RequestBody SubscribeRequest req) {
        logger.info("Subscribe request: user={} plan={} tier={}", req.getUserId(), req.getPlanDuration(), req.getTier());
        MembershipResponse response = membershipService.subscribe(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Membership subscribed successfully", response));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get active membership",
            description = "Track current membership details, expiry date, tier, and active benefits")
    public ResponseEntity<ApiResponse<MembershipResponse>> getMembership(@PathVariable Integer userId) {
        logger.info("Fetching membership for user: {}", userId);
        MembershipResponse response = membershipService.getMembership(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}/history")
    @Operation(summary = "Get membership history",
            description = "Returns full subscription history for a user in reverse chronological order")
    public ResponseEntity<ApiResponse<List<MembershipResponse>>> getMembershipHistory(@PathVariable Integer userId) {
        logger.info("Fetching membership history for user: {}", userId);
        List<MembershipResponse> history = membershipService.getMembershipHistory(userId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @PatchMapping("/user/{userId}/tier")
    @Operation(summary = "Upgrade or downgrade membership tier",
            description = "Change the tier of an active membership. Downgrade reduces benefits, upgrade unlocks more perks.")
    public ResponseEntity<ApiResponse<MembershipResponse>> changeTier(
            @PathVariable Integer userId,
            @Valid @RequestBody ChangeTierRequest req) {
        logger.info("Tier change request for user {}: newTier={}", userId, req.getNewTier());
        MembershipResponse response = membershipService.changeTier(userId, req);
        return ResponseEntity.ok(ApiResponse.success("Membership tier updated successfully", response));
    }

    @DeleteMapping("/user/{userId}/cancel")
    @Operation(summary = "Cancel membership",
            description = "Cancel the active membership for a user. Membership remains accessible until the end date.")
    public ResponseEntity<ApiResponse<MembershipResponse>> cancelMembership(
            @PathVariable Integer userId,
            @RequestBody(required = false) CancelMembershipRequest req) {
        logger.info("Cancel membership request for user: {}", userId);
        if (req == null) req = new CancelMembershipRequest();
        MembershipResponse response = membershipService.cancelMembership(userId, req);
        return ResponseEntity.ok(ApiResponse.success("Membership cancelled successfully", response));
    }

    @GetMapping("/{membershipId}/audit-log")
    @Operation(summary = "Get membership audit log",
            description = "Returns the full audit trail (subscribe, upgrade, downgrade, cancel, auto-tier events)")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLog(@PathVariable Integer membershipId) {
        logger.info("Fetching audit log for membership: {}", membershipId);
        List<AuditLogResponse> logs = membershipService.getAuditLog(membershipId);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    @PostMapping("/user/{userId}/evaluate-tier")
    @Operation(summary = "Manually trigger tier evaluation",
            description = "Force immediate re-evaluation of tier eligibility based on current order stats")
    public ResponseEntity<ApiResponse<String>> evaluateTier(@PathVariable Integer userId) {
        logger.info("Manual tier evaluation triggered for user: {}", userId);
        boolean upgraded = membershipService.autoEvaluateTier(userId);
        String msg = upgraded ? "Tier upgraded based on current activity" : "Tier unchanged - no better tier qualifies";
        return ResponseEntity.ok(ApiResponse.success(msg, "Evaluation complete"));
    }
}
