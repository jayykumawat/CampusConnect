package com.jay.campusconnect.dto.response;

import java.time.LocalDateTime;

public record AnnouncementResponse(
        Long id,
        Long eventId,
        String eventTitle,
        Long createdByUserId,
        String createdByUserName,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
