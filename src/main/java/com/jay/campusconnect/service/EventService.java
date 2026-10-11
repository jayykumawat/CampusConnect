package com.jay.campusconnect.service;

import com.jay.campusconnect.dto.request.EventCreateRequest;
import com.jay.campusconnect.dto.request.EventUpdateRequest;
import com.jay.campusconnect.dto.response.EventResponse;
import com.jay.campusconnect.enums.EventCategory;
import com.jay.campusconnect.enums.EventStatus;

import java.util.List;

public interface EventService {

    EventResponse createEvent(EventCreateRequest request);

    EventResponse getEventById(Long id);

    List<EventResponse> getAllEvents();

    List<EventResponse> getEventsByInstitutionId(Long institutionId);

    List<EventResponse> getEventsByFilters(EventStatus status, EventCategory category);

    List<EventResponse> getUpcomingEvents();

    EventResponse updateEvent(Long id, EventUpdateRequest request);

    void deleteEvent(Long id);
}
