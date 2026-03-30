package com.escrow.auth.controller;

import com.escrow.auth.dto.UserResponse;
import com.escrow.auth.entity.Role;
import com.escrow.auth.entity.User;
import com.escrow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<?> listUsers(
            @RequestHeader("X-User-Role") String userRole) {
        if (!isAdmin(userRole)) {
            return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
        }
        List<UserResponse> users = userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(
            @PathVariable UUID id,
            @RequestHeader("X-User-Role") String userRole) {
        if (!isAdmin(userRole)) {
            return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
        }
        return userRepository.findById(id)
                .map(u -> ResponseEntity.ok(UserResponse.from(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<?> changeRole(
            @PathVariable UUID id,
            @RequestHeader("X-User-Role") String userRole,
            @RequestBody Map<String, String> body) {
        if (!isAdmin(userRole)) {
            return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
        }
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            Role newRole = Role.valueOf(body.get("role").toUpperCase());
            user.setRole(newRole);
            userRepository.save(user);
            return ResponseEntity.ok(UserResponse.from(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role"));
        }
    }

    @PatchMapping("/users/{id}/toggle")
    public ResponseEntity<?> toggleEnabled(
            @PathVariable UUID id,
            @RequestHeader("X-User-Role") String userRole) {
        if (!isAdmin(userRole)) {
            return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
        }
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        return ResponseEntity.ok(UserResponse.from(user));
    }

    private boolean isAdmin(String role) {
        return "ADMINISTRATOR".equals(role);
    }
}
