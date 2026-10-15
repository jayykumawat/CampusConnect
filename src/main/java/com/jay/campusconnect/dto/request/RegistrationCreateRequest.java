package com.jay.campusconnect.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegistrationCreateRequest(
        @NotNull(message = "Event id is required")
        @Positive(message = "Event id must be greater than zero")
        Long eventId,

        @NotNull(message = "User id is required")
        @Positive(message = "User id must be greater than zero")
        Long userId
) {
}
