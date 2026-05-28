package com.firstclub.firstclub_membership.Controllers;

import com.firstclub.firstclub_membership.Models.User;
import com.firstclub.firstclub_membership.Requests.CreateUserRequest;
import com.firstclub.firstclub_membership.Requests.UpdateUserOrderStatsRequest;
import com.firstclub.firstclub_membership.Service.MembershipService;
import com.firstclub.firstclub_membership.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.firstclub.firstclub_membership.Responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MembershipService membershipService;

    @PostMapping
    @Operation(summary = "Create user", description = "Register a new user")
    public ResponseEntity<ApiResponse<String>> createUser(@Valid @RequestBody CreateUserRequest req) {
        userService.createUser(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<String>builder().success(true).message("User created successfully").build());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Integer userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping("/order-stats")
    @Operation(summary = "Update order stats",
            description = "Simulate order activity for a user. Updates order count + value and triggers tier re-evaluation.")
    public ResponseEntity<ApiResponse<User>> updateOrderStats(@Valid @RequestBody UpdateUserOrderStatsRequest req) {
        logger.info("Updating order stats for user: {}", req.getUserId());
        User updated = userService.updateOrderStats(req.getUserId(), req.getOrdersToAdd(), req.getOrderValueToAdd());
        membershipService.autoEvaluateTier(req.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Order stats updated and tier re-evaluated", updated));
    }
}
