package com.escrow.dispute_service.api;

import com.escrow.dispute_service.service.DisputeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/disputes")
public class DisputeController {

    private final DisputeService service;

    public DisputeController(DisputeService service) {
        this.service = service;
    }

    @PostMapping("/open")
    @PreAuthorize("hasAnyRole('DEPOSITOR','OPERATOR','ADMIN')")
    public DisputeDtos.DisputeCaseResponse open(@Valid @RequestBody DisputeDtos.OpenDisputeRequest request,
                                                Authentication auth) {
        return DisputeDtos.DisputeCaseResponse.from(service.open(request.dealId(), auth.getName(), request.reason()));
    }

    @GetMapping("/{dealId}")
    @PreAuthorize("hasAnyRole('DEPOSITOR','BENEFICIARY','OPERATOR','ADMIN')")
    public List<DisputeDtos.DisputeCaseResponse> history(@PathVariable UUID dealId) {
        return service.history(dealId).stream().map(DisputeDtos.DisputeCaseResponse::from).toList();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
