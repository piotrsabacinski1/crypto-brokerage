package com.example.brokeragechallenge.service;

import com.example.brokeragechallenge.domain.Account;
import com.example.brokeragechallenge.dto.AccountResponse;
import com.example.brokeragechallenge.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public Mono<AccountResponse> createAccount(String name, BigDecimal initialBalance) {
        return accountRepository.save(Account.create(name, initialBalance))
                .map(this::mapToResponse);
    }

    public Mono<AccountResponse> getAccount(Long id) {
        return accountRepository.findById(id)
                .map(this::mapToResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("Account not found")));
    }

    private AccountResponse mapToResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.getUsdBalance(),
                account.getBtcBalance()
        );
    }
}