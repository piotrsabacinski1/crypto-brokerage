package com.example.brokeragechallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BrokerageChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrokerageChallengeApplication.class, args);
    }

}
