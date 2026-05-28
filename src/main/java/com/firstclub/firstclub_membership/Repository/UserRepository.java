package com.firstclub.firstclub_membership.Repository;

import com.firstclub.firstclub_membership.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByMobNo(String mobNo);
}
