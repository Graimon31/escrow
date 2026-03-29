package com.escrow.escrow_account_service.api;

import com.escrow.escrow_account_service.service.EscrowAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/escrow-accounts")
public class EscrowAccountController {

    private final EscrowAccountService service;

    public EscrowAccountController(EscrowAccountService service) {
        this.service = service;
    }

    @PostMapping("/open")
    @PreAuthorize("hasAnyRole('DEPOSITOR','OPERATOR','ADMIN')")
    public EscrowAccountDtos.EscrowAccountResponse open(@Valid @RequestBody EscrowAccountDtos.OpenAccountRequest request) {
        return EscrowAccountDtos.EscrowAccountResponse.from(service.open(request.dealId(), request.amount(), request.currency()));
    }

    @GetMapping("/by-deal/{dealId}")
    public EscrowAccountDtos.EscrowAccountResponse byDeal(@PathVariable UUID dealId) {
        return EscrowAccountDtos.EscrowAccountResponse.from(service.byDeal(dealId));
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handle(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
