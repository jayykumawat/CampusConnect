package com.jay.campusconnect.service.impl;

import com.jay.campusconnect.dto.request.AnnouncementCreateRequest;
import com.jay.campusconnect.dto.request.AnnouncementUpdateRequest;
import com.jay.campusconnect.dto.response.AnnouncementResponse;
import com.jay.campusconnect.entity.Announcement;
import com.jay.campusconnect.entity.Event;
import com.jay.campusconnect.entity.User;
import com.jay.campusconnect.exception.ResourceNotFoundException;
import com.jay.campusconnect.mapper.AnnouncementMapper;
import com.jay.campusconnect.repository.AnnouncementRepository;
import com.jay.campusconnect.repository.EventRepository;
import com.jay.campusconnect.repository.UserRepository;
import com.jay.campusconnect.service.AnnouncementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final AnnouncementMapper announcementMapper;

    public AnnouncementServiceImpl(
            AnnouncementRepository announcementRepository,
            EventRepository eventRepository,
            UserRepository userRepository,
            AnnouncementMapper announcementMapper
    ) {
        this.announcementRepository = announcementRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.announcementMapper = announcementMapper;
    }

    @Override
    public AnnouncementResponse createAnnouncement(Long eventId, AnnouncementCreateRequest request) {
        Event event = findEvent(eventId);
        User createdBy = findUser(request.createdByUserId());
        Announcement announcement = announcementMapper.toEntity(request, event, createdBy);

        Announcement savedAnnouncement = announcementRepository.save(announcement);
        return announcementMapper.toResponse(savedAnnouncement);
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementResponse getAnnouncementById(Long id) {
        return announcementMapper.toResponse(findAnnouncement(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getAnnouncementsByEvent(Long eventId) {
        findEvent(eventId);
        return announcementRepository.findByEventId(eventId).stream()
                .map(announcementMapper::toResponse)
                .toList();
    }

    @Override
    public AnnouncementResponse updateAnnouncement(Long id, AnnouncementUpdateRequest request) {
        Announcement announcement = findAnnouncement(id);
        announcementMapper.updateEntity(announcement, request);

        Announcement savedAnnouncement = announcementRepository.save(announcement);
        return announcementMapper.toResponse(savedAnnouncement);
    }

    @Override
    public void deleteAnnouncement(Long id) {
        Announcement announcement = findAnnouncement(id);
        announcementRepository.delete(announcement);
    }

    private Announcement findAnnouncement(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Announcement not found with id " + id
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
