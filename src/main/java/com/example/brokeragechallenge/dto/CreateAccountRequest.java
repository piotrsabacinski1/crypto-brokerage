package com.example.brokeragechallenge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateAccountRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Balance is required")
        @PositiveOrZero(message = "Initial balance cannot be negative")
        BigDecimal usdBalance
) {}