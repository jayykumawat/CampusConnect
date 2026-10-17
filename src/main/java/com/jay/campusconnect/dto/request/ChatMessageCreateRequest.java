package com.jay.campusconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChatMessageCreateRequest(
        @NotBlank(message = "Message text is required")
        String messageText,

        // MVP only: this client-supplied ID is not an authenticated identity.
        @NotNull(message = "Sender user id is required")
        @Positive(message = "Sender user id must be greater than zero")
        Long senderUserId
) {
}
