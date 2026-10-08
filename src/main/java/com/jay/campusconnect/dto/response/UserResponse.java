package com.jay.campusconnect.dto.response;

import com.jay.campusconnect.enums.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        InstitutionResponse institution,
        String classOrYear,
        String department,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
