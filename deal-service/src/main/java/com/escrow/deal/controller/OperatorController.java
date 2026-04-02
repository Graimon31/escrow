package com.escrow.deal.controller;

import com.escrow.deal.dto.DealResponse;
import com.escrow.deal.entity.Deal;
import com.escrow.deal.entity.DealStatus;
import com.escrow.deal.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/operator")
@RequiredArgsConstructor
public class OperatorController {

    private final DealRepository dealRepository;

    @GetMapping("/deals")
    public ResponseEntity<?> listAllDeals(
            @RequestHeader("X-User-Role") String userRole,
            @RequestParam(required = false) String status) {
        if (!isOperatorOrAdmin(userRole)) {
            return ResponseEntity.status(403).body(Map.of("error", "Operator access required"));
        }
        List<Deal> deals;
        if (status != null && !status.isEmpty()) {
            try {
                DealStatus dealStatus = DealStatus.valueOf(status.toUpperCase());
                deals = dealRepository.findByStatusOrderByCreatedAtDesc(dealStatus);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid status"));
            }
        } else {
            deals = dealRepository.findAllByOrderByCreatedAtDesc();
        }
        return ResponseEntity.ok(deals.stream().map(DealResponse::from).toList());
    }

    @GetMapping("/deals/stats")
    public ResponseEntity<?> getStats(
            @RequestHeader("X-User-Role") String userRole) {
        if (!isOperatorOrAdmin(userRole)) {
            return ResponseEntity.status(403).body(Map.of("error", "Operator access required"));
        }
        long total = dealRepository.count();
        long disputed = dealRepository.countByStatus(DealStatus.DISPUTED);
        long active = dealRepository.countByStatusNotIn(List.of(
                DealStatus.COMPLETED, DealStatus.CANCELLED, DealStatus.REFUNDED, DealStatus.CLOSED));
        long completed = dealRepository.countByStatus(DealStatus.COMPLETED);
        return ResponseEntity.ok(Map.of(
                "total", total,
                "disputed", disputed,
                "active", active,
                "completed", completed
        ));
    }

    private boolean isOperatorOrAdmin(String role) {
        return "OPERATOR".equals(role) || "ADMINISTRATOR".equals(role);
    }
}
