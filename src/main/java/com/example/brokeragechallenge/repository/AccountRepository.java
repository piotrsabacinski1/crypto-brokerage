package com.example.brokeragechallenge.repository;

import com.example.brokeragechallenge.domain.Account;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends R2dbcRepository<Account, Long> {
}