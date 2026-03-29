package com.escrow.resolution_service.api;

import com.escrow.resolution_service.domain.ResolutionOutcome;
import com.escrow.resolution_service.service.ResolutionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/resolution")
public class ResolutionController {

    private final ResolutionService service;

    public ResolutionController(ResolutionService service) {
        this.service = service;
    }

    @PostMapping("/decide")
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public ResolutionDtos.ResolutionResponse decide(@Valid @RequestBody ResolutionDtos.ResolutionRequest request,
                                                    Authentication auth) {
        return ResolutionDtos.ResolutionResponse.from(
                service.resolve(request.dealId(), request.outcome(), auth.getName(), request.comment())
        );
    }

    @PostMapping("/{dealId}/release")
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public ResolutionDtos.ResolutionResponse release(@PathVariable UUID dealId,
                                                     @RequestParam(defaultValue = "Средства подлежат release") String comment,
                                                     Authentication auth) {
        return ResolutionDtos.ResolutionResponse.from(service.resolve(dealId, ResolutionOutcome.RELEASE, auth.getName(), comment));
    }

    @PostMapping("/{dealId}/refund")
    @PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
    public ResolutionDtos.ResolutionResponse refund(@PathVariable UUID dealId,
                                                    @RequestParam(defaultValue = "Средства подлежат refund") String comment,
                                                    Authentication auth) {
        return ResolutionDtos.ResolutionResponse.from(service.resolve(dealId, ResolutionOutcome.REFUND, auth.getName(), comment));
    }

    @GetMapping("/history/{dealId}")
    @PreAuthorize("hasAnyRole('DEPOSITOR','BENEFICIARY','OPERATOR','ADMIN')")
    public List<ResolutionDtos.ResolutionResponse> history(@PathVariable UUID dealId) {
        return service.history(dealId).stream().map(ResolutionDtos.ResolutionResponse::from).toList();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
