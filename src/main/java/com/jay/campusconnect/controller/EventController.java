package com.jay.campusconnect.controller;

import com.jay.campusconnect.dto.request.EventCreateRequest;
import com.jay.campusconnect.dto.request.EventUpdateRequest;
import com.jay.campusconnect.dto.response.EventResponse;
import com.jay.campusconnect.enums.EventCategory;
import com.jay.campusconnect.enums.EventStatus;
import com.jay.campusconnect.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> create(
            @Valid @RequestBody EventCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(request));
    }

    @GetMapping("/{id}")
    public EventResponse getById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping
    public List<EventResponse> getByFilters(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) EventCategory category
    ) {
        return eventService.getEventsByFilters(status, category);
    }

    @GetMapping("/institution/{institutionId}")
    public List<EventResponse> getByInstitutionId(@PathVariable Long institutionId) {
        return eventService.getEventsByInstitutionId(institutionId);
    }

    @GetMapping("/upcoming")
    public List<EventResponse> getUpcoming() {
        return eventService.getUpcomingEvents();
    }

    @PutMapping("/{id}")
    public EventResponse update(
            @PathVariable Long id,
            @Valid @RequestBody EventUpdateRequest request
    ) {
        return eventService.updateEvent(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}
