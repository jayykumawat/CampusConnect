package com.jay.campusconnect.dto.request;

import com.jay.campusconnect.enums.AudienceType;
import com.jay.campusconnect.enums.EventCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record EventUpdateRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Category is required")
        EventCategory category,

        @NotNull(message = "Date and time are required")
        LocalDateTime dateTime,

        @NotBlank(message = "Venue is required")
        String venue,

        @Positive(message = "Capacity must be greater than zero")
        Integer capacity,

        @NotNull(message = "Audience type is required")
        AudienceType audienceType
) {
}
