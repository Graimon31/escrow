package com.escrow.fulfillment_service.api;

import com.escrow.fulfillment_service.service.FulfillmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fulfillment")
public class FulfillmentController {

    private final FulfillmentService service;

    public FulfillmentController(FulfillmentService service) {
        this.service = service;
    }

    @PostMapping("/submit")
    @PreAuthorize("hasRole('BENEFICIARY')")
    public FulfillmentDtos.FulfillmentResponse submit(@Valid @RequestBody FulfillmentDtos.SubmitFulfillmentRequest request,
                                                      Authentication authentication) {
        return FulfillmentDtos.FulfillmentResponse.from(
                service.submit(request.dealId(), authentication.getName(), request.description(), request.documents())
        );
    }

    @GetMapping("/{dealId}")
    public FulfillmentDtos.FulfillmentResponse get(@PathVariable UUID dealId) {
        return FulfillmentDtos.FulfillmentResponse.from(service.getByDeal(dealId));
    }

    @GetMapping("/{dealId}/documents")
    public List<FulfillmentDtos.DocumentMetaResponse> documents(@PathVariable UUID dealId) {
        return service.documents(dealId).stream().map(FulfillmentDtos.DocumentMetaResponse::from).toList();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
