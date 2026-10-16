package com.jay.campusconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AnnouncementCreateRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Content is required")
        String content,

        @NotNull(message = "Creator user id is required")
        @Positive(message = "Creator user id must be greater than zero")
        Long createdByUserId
) {
}
