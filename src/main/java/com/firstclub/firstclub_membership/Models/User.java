package com.firstclub.firstclub_membership.Models;

import com.firstclub.firstclub_membership.Enums.UserCohort;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    @Column(unique = true)
    private String mobNo;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserCohort cohort = UserCohort.GENERAL;

    @Builder.Default
    private Integer totalOrderCount = 0;

    @Builder.Default
    private Double totalOrderValueCurrentMonth = 0.0;
}
