package com.escrow.auth_service.service;

import com.escrow.auth_service.config.SeedUsersProperties;
import com.escrow.auth_service.model.UserAccount;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SeedUserService {

    private final Map<String, UserAccount> users;

    public SeedUserService(SeedUsersProperties seedUsersProperties, PasswordEncoder passwordEncoder) {
        this.users = seedUsersProperties.getUsers().stream()
                .map(u -> new UserAccount(
                        u.getUsername(),
                        passwordEncoder.encode(u.getPassword()),
                        u.getRole(),
                        u.getDisplayName()))
                .collect(Collectors.toMap(UserAccount::username, Function.identity()));
    }

    public Optional<UserAccount> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public List<UserAccount> allUsers() {
        return users.values().stream().toList();
    }
}
