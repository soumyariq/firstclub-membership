package com.firstclub.firstclub_membership.Requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserOrderStatsRequest {
    @NotNull(message = "User ID is required")
    private Integer userId;

    @Positive(message = "Order count must be positive")
    private Integer ordersToAdd;

    @Positive(message = "Order value must be positive")
    private Double orderValueToAdd;
}
