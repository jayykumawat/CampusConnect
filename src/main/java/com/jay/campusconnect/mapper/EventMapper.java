package com.jay.campusconnect.mapper;

import com.jay.campusconnect.dto.request.EventCreateRequest;
import com.jay.campusconnect.dto.request.EventUpdateRequest;
import com.jay.campusconnect.dto.response.EventResponse;
import com.jay.campusconnect.entity.Event;
import com.jay.campusconnect.entity.Institution;
import com.jay.campusconnect.entity.User;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event toEntity(EventCreateRequest request, Institution institution, User createdBy) {
        Event event = new Event();
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setInstitution(institution);
        event.setCreatedBy(createdBy);
        event.setCategory(request.category());
        event.setDateTime(request.dateTime());
        event.setVenue(request.venue());
        event.setCapacity(request.capacity());
        event.setAudienceType(request.audienceType());
        return event;
    }

    public void updateEntity(Event event, EventUpdateRequest request) {
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setCategory(request.category());
        event.setDateTime(request.dateTime());
        event.setVenue(request.venue());
        event.setCapacity(request.capacity());
        event.setAudienceType(request.audienceType());
    }

    public EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getInstitution().getId(),
                event.getInstitution().getName(),
                event.getInstitution().getType(),
                event.getInstitution().getCity(),
                event.getCreatedBy().getId(),
                event.getCreatedBy().getName(),
                event.getCategory(),
                event.getDateTime(),
                event.getVenue(),
                event.getCapacity(),
                event.getAudienceType(),
                event.getStatus(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
