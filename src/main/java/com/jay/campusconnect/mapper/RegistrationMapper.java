package com.jay.campusconnect.mapper;

import com.jay.campusconnect.dto.response.RegistrationResponse;
import com.jay.campusconnect.entity.Event;
import com.jay.campusconnect.entity.Registration;
import com.jay.campusconnect.entity.User;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMapper {

    public Registration toEntity(Event event, User user) {
        Registration registration = new Registration();
        registration.setEvent(event);
        registration.setUser(user);
        return registration;
    }

    public RegistrationResponse toResponse(Registration registration) {
        return new RegistrationResponse(
                registration.getId(),
                registration.getEvent().getId(),
                registration.getEvent().getTitle(),
                registration.getEvent().getDateTime(),
                registration.getEvent().getVenue(),
                registration.getEvent().getCategory(),
                registration.getEvent().getStatus(),
                registration.getUser().getId(),
                registration.getUser().getName(),
                registration.getRegistrationTime(),
                registration.getStatus()
        );
    }
}
