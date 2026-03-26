package com.escrow.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class AuthServiceApplication {
  public static void main(String[] args) { SpringApplication.run(AuthServiceApplication.class, args); }
}

@RestController
@RequestMapping("/api/v1")
class InfoController {
  @GetMapping("/info")
  public String info() { return "auth-service up"; }
}
