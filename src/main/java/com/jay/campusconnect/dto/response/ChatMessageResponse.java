package com.jay.campusconnect.dto.response;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long eventId,
        Long senderUserId,
        String senderName,
        String messageText,
        LocalDateTime timestamp
) {
}
