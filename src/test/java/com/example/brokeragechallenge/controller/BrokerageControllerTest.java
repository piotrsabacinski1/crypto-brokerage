package com.example.brokeragechallenge.controller;

import com.example.brokeragechallenge.dto.AccountResponse;
import com.example.brokeragechallenge.dto.CreateAccountRequest;
import com.example.brokeragechallenge.dto.CreateOrderRequest;
import com.example.brokeragechallenge.dto.OrderResponse;
import com.example.brokeragechallenge.service.AccountService;
import com.example.brokeragechallenge.service.TradeOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(BrokerageController.class)
class BrokerageControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private TradeOrderService tradeOrderService;

    @Test
    void createAccount_ShouldReturnAccount() {
        // given
        CreateAccountRequest request = new CreateAccountRequest("Alice", new BigDecimal("1000"));
        AccountResponse response = new AccountResponse(1L, "Alice", new BigDecimal("1000"), BigDecimal.ZERO);

        when(accountService.createAccount("Alice", new BigDecimal("1000")))
                .thenReturn(Mono.just(response));

        // when & then
        webTestClient.post()
                .uri("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Alice")
                .jsonPath("$.usdBalance").isEqualTo(1000);
    }

    @Test
    void fetchAccountDetails_ShouldReturnAccount() {
        // given
        AccountResponse response = new AccountResponse(1L, "Alice", new BigDecimal("5000"), new BigDecimal("2.5"));

        when(accountService.getAccount(1L)).thenReturn(Mono.just(response));

        // when & then
        webTestClient.get()
                .uri("/api/accounts/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.usdBalance").isEqualTo(5000)
                .jsonPath("$.btcBalance").isEqualTo(2.5);
    }

    @Test
    void createLimitOrder_ShouldReturnPendingOrder() {
        // given
        CreateOrderRequest request = new CreateOrderRequest(1L, new BigDecimal("40000.00"), new BigDecimal("0.5"));

        OrderResponse response = new OrderResponse(
                10L,
                1L,
                new BigDecimal("40000.00"),
                new BigDecimal("0.5"),
                "PENDING",
                LocalDateTime.now()
        );

        when(tradeOrderService.createLimitOrder(any(CreateOrderRequest.class)))
                .thenReturn(Mono.just(response));

        // when & then
        webTestClient.post()
                .uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(10)
                .jsonPath("$.status").isEqualTo("PENDING")
                .jsonPath("$.priceLimit").isEqualTo(40000.00)
                .jsonPath("$.amount").isEqualTo(0.5);
    }

    @Test
    void fetchOrderDetails_ShouldReturnOrder() {
        // given
        OrderResponse response = new OrderResponse(
                10L,
                1L,
                new BigDecimal("35000.00"),
                new BigDecimal("1.0"),
                "EXECUTED",
                LocalDateTime.now()
        );

        when(tradeOrderService.getOrder(10L)).thenReturn(Mono.just(response));

        // when & then
        webTestClient.get()
                .uri("/api/orders/10")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(10)
                .jsonPath("$.status").isEqualTo("EXECUTED");
    }
}