package com.jay.campusconnect.dto.response;

import com.jay.campusconnect.enums.Role;

public record AuthResponse(
        Long id,
        String name,
        String email,
        Role role
) {
}
