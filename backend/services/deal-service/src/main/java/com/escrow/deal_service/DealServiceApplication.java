package com.escrow.deal_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DealServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DealServiceApplication.class, args);
    }
}

@RestController
@RequestMapping("/api/v1")
class HealthController {

    @GetMapping("/health")
    public String health() {
        return "OK: deal-service";
    }
}
