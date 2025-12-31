package com.example.brokeragechallenge.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotNull(message = "Account ID is required")
        Long accountId,

        @NotNull(message = "Price limit is required")
        @Positive(message = "Price limit must be positive")
        BigDecimal priceLimit,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount
) {}