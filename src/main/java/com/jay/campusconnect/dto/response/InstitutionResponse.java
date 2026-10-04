package com.jay.campusconnect.dto.response;

import com.jay.campusconnect.enums.InstitutionType;

import java.time.LocalDateTime;

public record InstitutionResponse(
        Long id,
        String name,
        InstitutionType type,
        String city,
        String address,
        String contactEmail,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
