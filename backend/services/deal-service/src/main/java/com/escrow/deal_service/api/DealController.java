package com.escrow.deal_service.api;

import com.escrow.deal_service.service.DealService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deals")
public class DealController {

    private final DealService dealService;

    public DealController(DealService dealService) {
        this.dealService = dealService;
    }

    @GetMapping
    public List<DealDtos.DealResponse> list() {
        return dealService.list().stream().map(DealDtos.DealResponse::from).toList();
    }

    @GetMapping("/{id}")
    public DealDtos.DealResponse get(@PathVariable UUID id) {
        return DealDtos.DealResponse.from(dealService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('DEPOSITOR')")
    public DealDtos.DealResponse create(@Valid @RequestBody DealDtos.CreateDealRequest request, Authentication auth) {
        return DealDtos.DealResponse.from(
                dealService.create(request.title(), request.amount(), request.currency(), auth.getName(), request.beneficiaryUsername())
        );
    }

    @PostMapping("/{id}/agree")
    @PreAuthorize("hasAnyRole('DEPOSITOR','BENEFICIARY')")
    public DealDtos.DealResponse agree(@PathVariable UUID id) {
        return DealDtos.DealResponse.from(dealService.agree(id));
    }

    @PostMapping("/{id}/open-escrow-account")
    @PreAuthorize("hasAnyRole('DEPOSITOR','OPERATOR','ADMIN')")
    public DealDtos.DealResponse openEscrowAccount(@PathVariable UUID id,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        String token = authorization.replace("Bearer ", "");
        return DealDtos.DealResponse.from(dealService.openEscrowAccount(id, token));
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
