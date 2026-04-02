package com.escrow.auth.dto;

import com.escrow.auth.entity.Role;
import com.escrow.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private Role role;
    private boolean enabled;

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole(), user.isEnabled());
    }
}
