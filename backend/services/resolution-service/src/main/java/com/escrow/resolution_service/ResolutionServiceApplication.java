package com.escrow.resolution_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ResolutionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResolutionServiceApplication.class, args);
    }
}

@RestController
@RequestMapping("/api/v1")
class HealthController {

    @GetMapping("/health")
    public String health() {
        return "OK: resolution-service";
    }
}
