package com.example.brokeragechallenge.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Table("trade_order")
public class TradeOrder {
    @Id
    private Long id;
    private Long accountId;
    private BigDecimal priceLimit;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;

    @Version
    private Long version;
}