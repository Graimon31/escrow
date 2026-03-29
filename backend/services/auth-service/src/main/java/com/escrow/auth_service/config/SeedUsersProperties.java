package com.escrow.auth_service.config;

import com.escrow.auth_service.model.Role;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.auth")
public class SeedUsersProperties {

    private String jwtSecret;
    private Long jwtTtlMinutes = 120L;
    private List<SeedUser> users = new ArrayList<>();

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public Long getJwtTtlMinutes() {
        return jwtTtlMinutes;
    }

    public void setJwtTtlMinutes(Long jwtTtlMinutes) {
        this.jwtTtlMinutes = jwtTtlMinutes;
    }

    public List<SeedUser> getUsers() {
        return users;
    }

    public void setUsers(List<SeedUser> users) {
        this.users = users;
    }

    public static class SeedUser {
        private String username;
        private String password;
        private Role role;
        private String displayName;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }
}
