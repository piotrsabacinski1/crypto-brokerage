package com.example.brokeragechallenge.service;

import com.example.brokeragechallenge.domain.TradeOrder;
import com.example.brokeragechallenge.repository.AccountRepository;
import com.example.brokeragechallenge.repository.TradeOrderRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceMonitor {

    private final WebClient.Builder webClientBuilder;
    private final TradeOrderRepository tradeOrderRepository;
    private final AccountRepository accountRepository;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        HttpClient httpClient = HttpClient.create(ConnectionProvider.newConnection())
                .responseTimeout(Duration.ofSeconds(2));

        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("http://127.0.0.1:5000")
                .build();
    }

    @Scheduled(fixedRate = 1000)
    public void checkMarket() {
        webClient.get()
                .uri("/btc-price")
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> new BigDecimal(response.get("price").toString()))

                .timeout(Duration.ofSeconds(2))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))

                .flatMap(this::processMatchingOrders)

                .onErrorResume(e -> {
                    log.warn("Market data unavailable: {}", e.getMessage());
                    return Mono.empty();
                })
                .subscribe();
    }

    @Transactional
    public Mono<Void> executeOrder(TradeOrder order, BigDecimal marketPrice) {
        log.info("Executing Order #{} at price {}", order.getId(), marketPrice);

        BigDecimal reservedCost = order.getPriceLimit().multiply(order.getAmount());
        BigDecimal actualCost = marketPrice.multiply(order.getAmount());
        BigDecimal refund = reservedCost.subtract(actualCost);

        return accountRepository.findById(order.getAccountId())
                .flatMap(account -> {
                    account.setBtcBalance(account.getBtcBalance().add(order.getAmount()));
                    if (refund.compareTo(BigDecimal.ZERO) > 0) {
                        account.setUsdBalance(account.getUsdBalance().add(refund));
                    }
                    return accountRepository.save(account);
                })
                .flatMap(savedAccount -> {
                    order.setStatus("EXECUTED");
                    return tradeOrderRepository.save(order);
                })
                .then();
    }

    private Mono<Void> processMatchingOrders(BigDecimal marketPrice) {
        log.info("Current Market Price: " + marketPrice);

        return tradeOrderRepository.findByStatusAndPriceLimitGreaterThanEqual("PENDING", marketPrice)
                .flatMap(order -> executeOrder(order, marketPrice))
                .then();
    }
}