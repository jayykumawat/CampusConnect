package com.jay.campusconnect.service;

import com.jay.campusconnect.dto.request.AnnouncementCreateRequest;
import com.jay.campusconnect.dto.request.AnnouncementUpdateRequest;
import com.jay.campusconnect.dto.response.AnnouncementResponse;

import java.util.List;

public interface AnnouncementService {

    AnnouncementResponse createAnnouncement(Long eventId, AnnouncementCreateRequest request);

    AnnouncementResponse getAnnouncementById(Long id);

    List<AnnouncementResponse> getAnnouncementsByEvent(Long eventId);

    AnnouncementResponse updateAnnouncement(Long id, AnnouncementUpdateRequest request);

    void deleteAnnouncement(Long id);
}
