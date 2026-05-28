package com.firstclub.firstclub_membership.Requests;

import com.firstclub.firstclub_membership.Enums.MembershipTier;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ChangeTierRequest {
    @NotNull(message = "New tier is required")
    private MembershipTier newTier;
    private String reason;
}
