package com.example.brokeragechallenge.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Long accountId,
        BigDecimal priceLimit,
        BigDecimal amount,
        String status,
        LocalDateTime createdAt
) {}