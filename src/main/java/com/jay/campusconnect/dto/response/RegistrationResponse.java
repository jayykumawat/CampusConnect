package com.jay.campusconnect.dto.response;

import com.jay.campusconnect.enums.EventCategory;
import com.jay.campusconnect.enums.EventStatus;
import com.jay.campusconnect.enums.RegistrationStatus;

import java.time.LocalDateTime;

public record RegistrationResponse(
        Long id,
        Long eventId,
        String eventTitle,
        LocalDateTime eventDateTime,
        String eventVenue,
        EventCategory eventCategory,
        EventStatus eventStatus,
        Long userId,
        String userName,
        LocalDateTime registrationTime,
        RegistrationStatus status
) {
}
