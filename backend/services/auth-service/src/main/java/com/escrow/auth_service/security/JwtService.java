package com.escrow.auth_service.security;

import com.escrow.auth_service.config.SeedUsersProperties;
import com.escrow.auth_service.model.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final SeedUsersProperties properties;
    private SecretKey key;

    public JwtService(SeedUsersProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(properties.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserAccount user) {
        Instant now = Instant.now();
        Instant exp = now.plus(properties.getJwtTtlMinutes(), ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(user.username())
                .claims(Map.of(
                        "role", user.role().name(),
                        "displayName", user.displayName()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
