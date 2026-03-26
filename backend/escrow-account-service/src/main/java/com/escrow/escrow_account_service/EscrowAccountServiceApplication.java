package com.escrow.escrow_account_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class EscrowAccountServiceApplication {
  public static void main(String[] args) { SpringApplication.run(EscrowAccountServiceApplication.class, args); }
}

@RestController
@RequestMapping("/api/v1")
class InfoController {
  @GetMapping("/info")
  public String info() { return "escrow-account-service up"; }
}
