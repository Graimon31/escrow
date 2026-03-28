package com.escrow.payment.controller;

import com.escrow.payment.dto.AccountResponse;
import com.escrow.payment.dto.TransactionResponse;
import com.escrow.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/account")
    public ResponseEntity<AccountResponse> getAccount(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(paymentService.getOrCreateAccount(userId));
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(
            @RequestHeader("X-User-Id") UUID userId) {
        try {
            List<TransactionResponse> transactions = paymentService.getTransactions(userId);
            return ResponseEntity.ok(transactions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
