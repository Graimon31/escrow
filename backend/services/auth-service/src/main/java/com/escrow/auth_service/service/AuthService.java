package com.escrow.auth_service.service;

import com.escrow.auth_service.api.AuthDtos;
import com.escrow.auth_service.model.UserAccount;
import com.escrow.auth_service.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final SeedUserService seedUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(SeedUserService seedUserService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.seedUserService = seedUserService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthDtos.LoginResponse login(String username, String password) {
        UserAccount user = seedUserService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Неверный логин или пароль"));

        if (!passwordEncoder.matches(password, user.password())) {
            throw new IllegalArgumentException("Неверный логин или пароль");
        }

        String token = jwtService.generateToken(user);
        return new AuthDtos.LoginResponse(token, "Bearer", user.role().name(), user.displayName());
    }

    public AuthDtos.UserInfoResponse userInfo(String username) {
        UserAccount user = seedUserService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        return new AuthDtos.UserInfoResponse(user.username(), user.role().name(), user.displayName());
    }
}
