package com.jay.campusconnect.service.impl;

import com.jay.campusconnect.dto.request.EventCreateRequest;
import com.jay.campusconnect.dto.request.EventUpdateRequest;
import com.jay.campusconnect.dto.response.EventResponse;
import com.jay.campusconnect.entity.Event;
import com.jay.campusconnect.entity.Institution;
import com.jay.campusconnect.entity.User;
import com.jay.campusconnect.enums.EventCategory;
import com.jay.campusconnect.enums.EventStatus;
import com.jay.campusconnect.exception.DuplicateResourceException;
import com.jay.campusconnect.exception.ResourceNotFoundException;
import com.jay.campusconnect.mapper.EventMapper;
import com.jay.campusconnect.repository.ChatMessageRepository;
import com.jay.campusconnect.repository.EventRepository;
import com.jay.campusconnect.repository.InstitutionRepository;
import com.jay.campusconnect.repository.RegistrationRepository;
import com.jay.campusconnect.repository.UserRepository;
import com.jay.campusconnect.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final EventMapper eventMapper;
    private final Clock clock;

    public EventServiceImpl(
            EventRepository eventRepository,
            InstitutionRepository institutionRepository,
            UserRepository userRepository,
            RegistrationRepository registrationRepository,
            ChatMessageRepository chatMessageRepository,
            EventMapper eventMapper
    ) {
        this.eventRepository = eventRepository;
        this.institutionRepository = institutionRepository;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.eventMapper = eventMapper;
        this.clock = Clock.systemDefaultZone();
    }

    @Override
    public EventResponse createEvent(EventCreateRequest request) {
        LocalDateTime now = LocalDateTime.now(clock);
        if (!request.dateTime().isAfter(now)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Event date and time must be in the future"
            );
        }

        Institution institution = findInstitution(request.institutionId());
        User createdBy = findUser(request.createdByUserId());
        Event event = eventMapper.toEntity(request, institution, createdBy);

        Event savedEvent = eventRepository.save(event);
        return eventMapper.toResponse(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        return eventMapper.toResponse(findEvent(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByInstitutionId(Long institutionId) {
        return eventRepository.findByInstitution(findInstitution(institutionId)).stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByFilters(EventStatus status, EventCategory category) {
        List<Event> events;
        if (status != null) {
            events = eventRepository.findByStatus(status);
        } else if (category != null) {
            events = eventRepository.findByCategory(category);
        } else {
            events = eventRepository.findAll();
        }

        return events.stream()
                .filter(event -> category == null || event.getCategory() == category)
                .map(eventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getUpcomingEvents() {
        return eventRepository.findByDateTimeAfterOrderByDateTimeAsc(LocalDateTime.now(clock)).stream()
                .filter(event -> event.getStatus() == EventStatus.ACTIVE)
                .map(eventMapper::toResponse)
                .toList();
    }

    @Override
    public EventResponse updateEvent(Long id, EventUpdateRequest request) {
        Event event = findEvent(id);
        LocalDateTime requestedDateTime = request.dateTime();
        if (!requestedDateTime.equals(event.getDateTime())
                && requestedDateTime.isBefore(LocalDateTime.now(clock))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Event date and time cannot be changed to a past time"
            );
        }

        eventMapper.updateEntity(event, request);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toResponse(savedEvent);
    }

    @Override
    public void deleteEvent(Long id) {
        Event event = findEvent(id);
        if (!registrationRepository.findByEvent(event).isEmpty()
                || !chatMessageRepository.findByEventOrderByTimestampAsc(event).isEmpty()) {
            throw new DuplicateResourceException(
                    "Event cannot be deleted because it has registrations or chat messages"
            );
        }
        eventRepository.delete(event);
    }

    private Event findEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + id));
    }

    private Institution findInstitution(Long id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id " + id));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }
}
