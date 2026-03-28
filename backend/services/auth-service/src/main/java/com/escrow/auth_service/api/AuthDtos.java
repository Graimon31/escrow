package com.escrow.auth_service.api;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    public record LoginResponse(String accessToken, String tokenType, String role, String displayName) {}

    public record UserInfoResponse(String username, String role, String displayName) {}
}
