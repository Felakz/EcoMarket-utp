package com.ecomarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EcoMarketApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcoMarketApplication.class, args);
    }
}
