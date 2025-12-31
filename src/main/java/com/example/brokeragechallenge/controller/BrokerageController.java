package com.example.brokeragechallenge.controller;

import com.example.brokeragechallenge.dto.AccountResponse;
import com.example.brokeragechallenge.dto.CreateAccountRequest;
import com.example.brokeragechallenge.dto.CreateOrderRequest;
import com.example.brokeragechallenge.dto.OrderResponse;
import com.example.brokeragechallenge.service.AccountService;
import com.example.brokeragechallenge.service.TradeOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BrokerageController {

    private final AccountService accountService;
    private final TradeOrderService tradeOrderService;

    @PostMapping("/accounts")
    public Mono<AccountResponse> createAccount(@RequestBody @Valid CreateAccountRequest request) {
        return accountService.createAccount(request.name(), request.usdBalance());
    }

    @GetMapping("/accounts/{id}")
    public Mono<AccountResponse> fetchAccountDetails(@PathVariable Long id) {
        return accountService.getAccount(id);
    }

    @PostMapping("/orders")
    public Mono<OrderResponse> createLimitOrder(@RequestBody @Valid CreateOrderRequest request) {
        return tradeOrderService.createLimitOrder(request);
    }

    @GetMapping("/orders/{id}")
    public Mono<OrderResponse> fetchOrderDetails(@PathVariable Long id) {
        return tradeOrderService.getOrder(id);
    }
}