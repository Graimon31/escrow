package com.escrow.auth_service.model;

public record UserAccount(String username, String password, Role role, String displayName) {
}
