package com.example.brokeragechallenge.dto;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String name,
        BigDecimal usdBalance,
        BigDecimal btcBalance
) {}