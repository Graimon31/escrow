package com.escrow.review_service.api;

import com.escrow.review_service.domain.ReviewActionType;
import com.escrow.review_service.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @PostMapping("/action")
    @PreAuthorize("hasRole('DEPOSITOR')")
    public ReviewDtos.ReviewActionResponse action(@Valid @RequestBody ReviewDtos.ReviewActionRequest request,
                                                  Authentication authentication) {
        return ReviewDtos.ReviewActionResponse.from(
                service.act(request.dealId(), request.action(), authentication.getName(), request.comment())
        );
    }

    @GetMapping("/history/{dealId}")
    public List<ReviewDtos.ReviewActionResponse> history(@PathVariable UUID dealId) {
        return service.history(dealId).stream().map(ReviewDtos.ReviewActionResponse::from).toList();
    }

    @PostMapping("/{dealId}/accept")
    @PreAuthorize("hasRole('DEPOSITOR')")
    public ReviewDtos.ReviewActionResponse accept(@PathVariable UUID dealId, @RequestParam(defaultValue = "Принято") String comment, Authentication auth) {
        return ReviewDtos.ReviewActionResponse.from(service.act(dealId, ReviewActionType.ACCEPT, auth.getName(), comment));
    }

    @PostMapping("/{dealId}/reject")
    @PreAuthorize("hasRole('DEPOSITOR')")
    public ReviewDtos.ReviewActionResponse reject(@PathVariable UUID dealId, @RequestParam(defaultValue = "Отклонено") String comment, Authentication auth) {
        return ReviewDtos.ReviewActionResponse.from(service.act(dealId, ReviewActionType.REJECT, auth.getName(), comment));
    }

    @PostMapping("/{dealId}/correction")
    @PreAuthorize("hasRole('DEPOSITOR')")
    public ReviewDtos.ReviewActionResponse correction(@PathVariable UUID dealId, @RequestParam(defaultValue = "Требуется доработка") String comment, Authentication auth) {
        return ReviewDtos.ReviewActionResponse.from(service.act(dealId, ReviewActionType.CORRECTION, auth.getName(), comment));
    }

    @PostMapping("/{dealId}/dispute")
    @PreAuthorize("hasAnyRole('DEPOSITOR','OPERATOR','ADMIN')")
    public ReviewDtos.ReviewActionResponse dispute(@PathVariable UUID dealId, @RequestParam(defaultValue = "Открыт спор") String comment, Authentication auth) {
        return ReviewDtos.ReviewActionResponse.from(service.act(dealId, ReviewActionType.DISPUTE, auth.getName(), comment));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
