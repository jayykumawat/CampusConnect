package com.jay.campusconnect.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AnnouncementUpdateRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Content is required")
        String content
) {
}
