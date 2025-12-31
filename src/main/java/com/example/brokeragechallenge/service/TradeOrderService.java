package com.example.brokeragechallenge.service;

import com.example.brokeragechallenge.domain.TradeOrder;
import com.example.brokeragechallenge.dto.CreateOrderRequest;
import com.example.brokeragechallenge.dto.OrderResponse;
import com.example.brokeragechallenge.repository.AccountRepository;
import com.example.brokeragechallenge.repository.TradeOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TradeOrderService {

    private final TradeOrderRepository tradeOrderRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Mono<OrderResponse> createLimitOrder(CreateOrderRequest request) {
        BigDecimal totalCost = request.priceLimit().multiply(request.amount());

        return accountRepository.findById(request.accountId())
                .switchIfEmpty(Mono.error(new RuntimeException("Account not found")))
                .flatMap(account -> {
                    if (account.getUsdBalance().compareTo(totalCost) < 0) {
                        return Mono.error(new RuntimeException("Insufficient USD funds"));
                    }
                    account.setUsdBalance(account.getUsdBalance().subtract(totalCost));

                    return accountRepository.save(account)
                            .then(saveOrder(request));
                })
                .map(this::mapToResponse);
    }

    public Mono<OrderResponse> getOrder(Long id) {
        return tradeOrderRepository.findById(id)
                .map(this::mapToResponse);
    }

    private Mono<TradeOrder> saveOrder(CreateOrderRequest request) {
        TradeOrder order = new TradeOrder();
        order.setAccountId(request.accountId());
        order.setPriceLimit(request.priceLimit());
        order.setAmount(request.amount());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        return tradeOrderRepository.save(order);
    }

    private OrderResponse mapToResponse(TradeOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getAccountId(),
                order.getPriceLimit(),
                order.getAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}