package com.example.brokeragechallenge.service;

import com.example.brokeragechallenge.domain.Account;
import com.example.brokeragechallenge.domain.TradeOrder;
import com.example.brokeragechallenge.dto.CreateOrderRequest;
import com.example.brokeragechallenge.dto.OrderResponse;
import com.example.brokeragechallenge.repository.AccountRepository;
import com.example.brokeragechallenge.repository.TradeOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeOrderServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TradeOrderRepository tradeOrderRepository;

    @InjectMocks
    private TradeOrderService tradeOrderService;

    @Test
    void createLimitOrder_Success() {
        // given: Account has $5000
        Account account = new Account();
        account.setId(1L);
        account.setUsdBalance(new BigDecimal("5000"));

        // and: Request to buy $1000 worth (limit 1000 * amount 1)
        CreateOrderRequest request = new CreateOrderRequest(1L, new BigDecimal("1000"), new BigDecimal("1"));

        when(accountRepository.findById(1L)).thenReturn(Mono.just(account));
        when(accountRepository.save(any(Account.class))).thenReturn(Mono.just(account));

        TradeOrder savedOrder = new TradeOrder();
        savedOrder.setId(10L);
        savedOrder.setAccountId(1L);
        savedOrder.setPriceLimit(new BigDecimal("1000"));
        savedOrder.setAmount(new BigDecimal("1"));
        savedOrder.setStatus("PENDING");

        when(tradeOrderRepository.save(any(TradeOrder.class))).thenReturn(Mono.just(savedOrder));

        // when
        Mono<OrderResponse> result = tradeOrderService.createLimitOrder(request);

        // then
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.id().equals(10L) &&
                                response.status().equals("PENDING")
                )
                .verifyComplete();
    }

    @Test
    void createLimitOrder_InsufficientFunds() {
        // given: Account has only $500
        Account account = new Account();
        account.setId(1L);
        account.setUsdBalance(new BigDecimal("500"));

        // and: Request costs $1000
        CreateOrderRequest request = new CreateOrderRequest(1L, new BigDecimal("1000"), new BigDecimal("1"));

        when(accountRepository.findById(1L)).thenReturn(Mono.just(account));

        // when
        Mono<OrderResponse> result = tradeOrderService.createLimitOrder(request);

        // then
        StepVerifier.create(result)
                .expectErrorMessage("Insufficient USD funds")
                .verify();
    }
}