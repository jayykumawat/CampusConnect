package com.jay.campusconnect.service.impl;

import com.jay.campusconnect.dto.request.RegistrationCreateRequest;
import com.jay.campusconnect.dto.response.RegistrationResponse;
import com.jay.campusconnect.entity.Event;
import com.jay.campusconnect.entity.Registration;
import com.jay.campusconnect.entity.User;
import com.jay.campusconnect.enums.EventStatus;
import com.jay.campusconnect.enums.RegistrationStatus;
import com.jay.campusconnect.exception.DuplicateResourceException;
import com.jay.campusconnect.exception.ResourceNotFoundException;
import com.jay.campusconnect.mapper.RegistrationMapper;
import com.jay.campusconnect.repository.EventRepository;
import com.jay.campusconnect.repository.RegistrationRepository;
import com.jay.campusconnect.repository.UserRepository;
import com.jay.campusconnect.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RegistrationMapper registrationMapper;
    private final Clock clock;

    public RegistrationServiceImpl(
            RegistrationRepository registrationRepository,
            EventRepository eventRepository,
            UserRepository userRepository,
            RegistrationMapper registrationMapper
    ) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.registrationMapper = registrationMapper;
        this.clock = Clock.systemDefaultZone();
    }

    @Override
    public RegistrationResponse createRegistration(RegistrationCreateRequest request) {
        Event event = eventRepository.findByIdForUpdate(request.eventId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event not found with id " + request.eventId()
                ));

        if (event.getStatus() != EventStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Registration is only available for active events"
            );
        }
        if (!event.getDateTime().isAfter(LocalDateTime.now(clock))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Registration is only available before the event date and time"
            );
        }

        User user = findUser(request.userId());
        Registration existingRegistration = registrationRepository
                .findByEventAndUser(event, user)
                .orElse(null);

        if (existingRegistration != null) {
            if (existingRegistration.getStatus() == RegistrationStatus.REGISTERED) {
                throw new DuplicateResourceException(
                        "User is already registered for event with id " + event.getId()
                );
            }
            if (existingRegistration.getStatus() == RegistrationStatus.WAITLIST) {
                throw new DuplicateResourceException(
                        "User is already waitlisted for event with id " + event.getId()
                );
            }
        }

        ensureCapacityAvailable(event);

        if (existingRegistration != null) {
            existingRegistration.setStatus(RegistrationStatus.REGISTERED);
            return registrationMapper.toResponse(registrationRepository.save(existingRegistration));
        }

        Registration registration = registrationMapper.toEntity(event, user);
        return registrationMapper.toResponse(registrationRepository.save(registration));
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationResponse getRegistrationById(Long id) {
        return registrationMapper.toResponse(findRegistration(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationResponse> getRegistrationsByEventId(Long eventId) {
        Event event = findEvent(eventId);
        return registrationRepository.findByEvent(event).stream()
                .map(registrationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationResponse> getRegistrationsByUserId(Long userId) {
        User user = findUser(userId);
        return registrationRepository.findByUser(user).stream()
                .map(registrationMapper::toResponse)
                .toList();
    }

    @Override
    public RegistrationResponse cancelRegistration(Long id) {
        Registration registration = findRegistration(id);
        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new DuplicateResourceException(
                    "Registration is already cancelled with id " + id
            );
        }

        registration.setStatus(RegistrationStatus.CANCELLED);
        return registrationMapper.toResponse(registrationRepository.save(registration));
    }

    private void ensureCapacityAvailable(Event event) {
        Integer capacity = event.getCapacity();
        if (capacity == null) {
            return;
        }

        long registeredCount = registrationRepository.countByEventAndStatus(
                event,
                RegistrationStatus.REGISTERED
        );
        if (registeredCount >= capacity) {
            throw new DuplicateResourceException(
                    "Event is at capacity with id " + event.getId()
            );
        }
    }

    private Registration findRegistration(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registration not found with id " + id
                ));
    }

    private Event findEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + id));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }
}
