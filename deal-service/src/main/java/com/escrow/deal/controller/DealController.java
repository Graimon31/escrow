package com.escrow.deal.controller;

import com.escrow.deal.dto.CreateDealRequest;
import com.escrow.deal.dto.DealEventResponse;
import com.escrow.deal.dto.DealResponse;
import com.escrow.deal.service.DealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;

    @PostMapping
    public ResponseEntity<?> createDeal(
            @Valid @RequestBody CreateDealRequest request,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole) {
        try {
            DealResponse response = dealService.createDeal(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<DealResponse>> listDeals(
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(dealService.listDeals(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDeal(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        try {
            return ResponseEntity.ok(dealService.getDeal(id, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<?> getDealEvents(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        try {
            return ResponseEntity.ok(dealService.getDealEvents(id, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitDeal(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole) {
        return handleAction(() -> dealService.submitDeal(id, userId, userRole));
    }

    @PostMapping("/{id}/agree")
    public ResponseEntity<?> agreeDeal(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole) {
        return handleAction(() -> dealService.agreeDeal(id, userId, userRole));
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<?> declineDeal(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole) {
        return handleAction(() -> dealService.declineDeal(id, userId, userRole));
    }

    @PostMapping("/{id}/fund")
    public ResponseEntity<?> fundDeal(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole) {
        return handleAction(() -> dealService.fundDeal(id, userId, userRole));
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<?> deliverDeal(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole) {
        return handleAction(() -> dealService.deliverDeal(id, userId, userRole));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmDeal(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole) {
        return handleAction(() -> dealService.confirmDeal(id, userId, userRole));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectDeal(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole) {
        return handleAction(() -> dealService.rejectDeal(id, userId, userRole));
    }

    @PostMapping("/{id}/dispute")
    public ResponseEntity<?> disputeDeal(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole) {
        return handleAction(() -> dealService.disputeDeal(id, userId, userRole));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<?> resolveDispute(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestBody Map<String, String> body) {
        return handleAction(() -> dealService.resolveDispute(id, userId, userRole, body.get("resolution")));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelDeal(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole) {
        return handleAction(() -> dealService.cancelDeal(id, userId, userRole));
    }

    private ResponseEntity<?> handleAction(java.util.function.Supplier<DealResponse> action) {
        try {
            return ResponseEntity.ok(action.get());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
