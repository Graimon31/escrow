package com.escrow.payment.controller;

import com.escrow.payment.dto.HoldRequest;
import com.escrow.payment.dto.ReleaseRequest;
import com.escrow.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/payments")
@RequiredArgsConstructor
public class InternalPaymentController {

    private final PaymentService paymentService;

    @PostMapping("/hold")
    public ResponseEntity<?> holdFunds(@Valid @RequestBody HoldRequest request) {
        try {
            paymentService.holdFunds(request.getDealId(), request.getDepositorId(), request.getAmount());
            return ResponseEntity.ok(Map.of("status", "HELD"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/release")
    public ResponseEntity<?> releaseFunds(@Valid @RequestBody ReleaseRequest request) {
        try {
            paymentService.releaseFunds(request.getDealId(), request.getBeneficiaryId());
            return ResponseEntity.ok(Map.of("status", "RELEASED"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refundFunds(
            @RequestParam UUID dealId,
            @RequestParam UUID depositorId) {
        try {
            paymentService.refundFunds(dealId, depositorId);
            return ResponseEntity.ok(Map.of("status", "REFUNDED"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
