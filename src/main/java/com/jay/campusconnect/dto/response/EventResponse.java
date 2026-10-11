package com.jay.campusconnect.dto.response;

import com.jay.campusconnect.enums.AudienceType;
import com.jay.campusconnect.enums.EventCategory;
import com.jay.campusconnect.enums.EventStatus;
import com.jay.campusconnect.enums.InstitutionType;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        Long institutionId,
        String institutionName,
        InstitutionType institutionType,
        String institutionCity,
        Long createdByUserId,
        String createdByUserName,
        EventCategory category,
        LocalDateTime dateTime,
        String venue,
        Integer capacity,
        AudienceType audienceType,
        EventStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
