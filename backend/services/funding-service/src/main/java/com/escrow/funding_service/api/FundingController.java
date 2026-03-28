package com.escrow.funding_service.api;

import com.escrow.funding_service.domain.AuditEvent;
import com.escrow.funding_service.service.FundingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/funding")
public class FundingController {

    private final FundingService fundingService;

    public FundingController(FundingService fundingService) {
        this.fundingService = fundingService;
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('DEPOSITOR','OPERATOR','ADMIN')")
    public FundingDtos.DepositResponse deposit(
            @Valid @RequestBody FundingDtos.DepositRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            Authentication authentication) {

        String key = idempotencyKey != null ? idempotencyKey : UUID.randomUUID().toString();
        return FundingDtos.DepositResponse.from(
                fundingService.deposit(request.dealId(), request.amount(), request.currency(), authentication.getName(), key)
        );
    }

    @GetMapping("/audit/{dealId}")
    @PreAuthorize("hasAnyRole('DEPOSITOR','OPERATOR','ADMIN')")
    public List<FundingDtos.AuditRecordResponse> audit(@PathVariable UUID dealId) {
        return fundingService.auditTrail(dealId).stream().map(FundingDtos.AuditRecordResponse::from).toList();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
