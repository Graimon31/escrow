package com.escrow.auth_service.api;

import com.escrow.auth_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.LoginResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request.username(), request.password()));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthDtos.UserInfoResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.userInfo(authentication.getName()));
    }

    @GetMapping("/admin-zone")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminZone() {
        return ResponseEntity.ok("Доступ только для ADMIN");
    }

    @GetMapping("/operator-zone")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    public ResponseEntity<String> operatorZone() {
        return ResponseEntity.ok("Доступ для OPERATOR или ADMIN");
    }

    @GetMapping("/depositor-zone")
    @PreAuthorize("hasRole('DEPOSITOR')")
    public ResponseEntity<String> depositorZone() {
        return ResponseEntity.ok("Доступ только для DEPOSITOR");
    }

    @GetMapping("/beneficiary-zone")
    @PreAuthorize("hasRole('BENEFICIARY')")
    public ResponseEntity<String> beneficiaryZone() {
        return ResponseEntity.ok("Доступ только для BENEFICIARY");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
