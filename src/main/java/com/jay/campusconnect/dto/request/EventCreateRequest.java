package com.jay.campusconnect.dto.request;

import com.jay.campusconnect.enums.AudienceType;
import com.jay.campusconnect.enums.EventCategory;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record EventCreateRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Institution id is required")
        Long institutionId,

        @NotNull(message = "Creator user id is required")
        Long createdByUserId,

        @NotNull(message = "Category is required")
        EventCategory category,

        @NotNull(message = "Date and time are required")
        @Future(message = "Event date and time must be in the future")
        LocalDateTime dateTime,

        @NotBlank(message = "Venue is required")
        String venue,

        @Positive(message = "Capacity must be greater than zero")
        Integer capacity,

        @NotNull(message = "Audience type is required")
        AudienceType audienceType
) {
}
