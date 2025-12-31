package com.example.brokeragechallenge.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;

@Data
@Table("account")
public class Account {
    @Id
    private Long id;
    private String name;
    private BigDecimal usdBalance;
    private BigDecimal btcBalance;

    @Version
    private Long version;

    public static Account create(String name, BigDecimal usdBalance) {
        Account a = new Account();
        a.setName(name);
        a.setUsdBalance(usdBalance);
        a.setBtcBalance(BigDecimal.ZERO);
        return a;
    }
}