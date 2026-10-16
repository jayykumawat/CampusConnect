package com.jay.campusconnect.controller;

import com.jay.campusconnect.dto.request.AnnouncementCreateRequest;
import com.jay.campusconnect.dto.request.AnnouncementUpdateRequest;
import com.jay.campusconnect.dto.response.AnnouncementResponse;
import com.jay.campusconnect.service.AnnouncementService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping("/events/{eventId}/announcements")
    public ResponseEntity<AnnouncementResponse> create(
            @PathVariable Long eventId,
            @Valid @RequestBody AnnouncementCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(announcementService.createAnnouncement(eventId, request));
    }

    @GetMapping("/announcements/{id}")
    public AnnouncementResponse getById(@PathVariable Long id) {
        return announcementService.getAnnouncementById(id);
    }

    @GetMapping("/events/{eventId}/announcements")
    public List<AnnouncementResponse> getByEvent(@PathVariable Long eventId) {
        return announcementService.getAnnouncementsByEvent(eventId);
    }

    @PutMapping("/announcements/{id}")
    public AnnouncementResponse update(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementUpdateRequest request
    ) {
        return announcementService.updateAnnouncement(id, request);
    }

    @DeleteMapping("/announcements/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
    }
}
