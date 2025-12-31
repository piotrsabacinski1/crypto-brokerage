package com.example.brokeragechallenge.repository;

import com.example.brokeragechallenge.domain.TradeOrder;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

@Repository
public interface TradeOrderRepository extends R2dbcRepository<TradeOrder, Long> {
    // Find orders that are PENDING and the Limit Price is >= Current Market Price
    Flux<TradeOrder> findByStatusAndPriceLimitGreaterThanEqual(String status, BigDecimal price);
}