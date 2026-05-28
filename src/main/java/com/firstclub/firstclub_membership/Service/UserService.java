package com.firstclub.firstclub_membership.Service;

import com.firstclub.firstclub_membership.Enums.UserCohort;
import com.firstclub.firstclub_membership.Exceptions.BusinessException;
import com.firstclub.firstclub_membership.Exceptions.ResourceNotFoundException;
import com.firstclub.firstclub_membership.Models.User;
import com.firstclub.firstclub_membership.Repository.UserRepository;
import com.firstclub.firstclub_membership.Requests.CreateUserRequest;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public User createUser(CreateUserRequest req) {
        logger.info("Creating user with email: {}", req.getEmail());

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException("User already exists with email: " + req.getEmail());
        }
        if (userRepository.existsByMobNo(req.getMobNo())) {
            throw new BusinessException("User already exists with mobile: " + req.getMobNo());
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .mobNo(req.getMobNo())
                .cohort(req.getCohort() != null ? req.getCohort() : UserCohort.GENERAL)
                .totalOrderCount(0)
                .totalOrderValueCurrentMonth(0.0)
                .build();

        user = userRepository.save(user);
        logger.info("User created with ID: {}", user.getUserId());
        return user;
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
    }

    public User updateOrderStats(Integer userId, Integer ordersToAdd, Double valueToAdd) {
        logger.info("Updating order stats for user: {}", userId);
        User user = getUserById(userId);
        if (ordersToAdd != null) {
            user.setTotalOrderCount(user.getTotalOrderCount() + ordersToAdd);
        }
        if (valueToAdd != null) {
            user.setTotalOrderValueCurrentMonth(user.getTotalOrderValueCurrentMonth() + valueToAdd);
        }
        return userRepository.save(user);
    }

    public void resetMonthlyOrderValues() {
        logger.info("Resetting monthly order values for all users");
        userRepository.findAll().forEach(u -> {
            u.setTotalOrderValueCurrentMonth(0.0);
            userRepository.save(u);
        });
    }
}
