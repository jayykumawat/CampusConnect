package com.jay.campusconnect.mapper;

import com.jay.campusconnect.dto.request.AnnouncementCreateRequest;
import com.jay.campusconnect.dto.request.AnnouncementUpdateRequest;
import com.jay.campusconnect.dto.response.AnnouncementResponse;
import com.jay.campusconnect.entity.Announcement;
import com.jay.campusconnect.entity.Event;
import com.jay.campusconnect.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementMapper {

    public Announcement toEntity(
            AnnouncementCreateRequest request,
            Event event,
            User createdBy
    ) {
        Announcement announcement = new Announcement();
        announcement.setEvent(event);
        announcement.setCreatedBy(createdBy);
        announcement.setTitle(request.title());
        announcement.setContent(request.content());
        return announcement;
    }

    public void updateEntity(
            Announcement announcement,
            AnnouncementUpdateRequest request
    ) {
        announcement.setTitle(request.title());
        announcement.setContent(request.content());
    }

    public AnnouncementResponse toResponse(Announcement announcement) {
        return new AnnouncementResponse(
                announcement.getId(),
                announcement.getEvent().getId(),
                announcement.getEvent().getTitle(),
                announcement.getCreatedBy().getId(),
                announcement.getCreatedBy().getName(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getCreatedAt(),
                announcement.getUpdatedAt()
        );
    }
}
