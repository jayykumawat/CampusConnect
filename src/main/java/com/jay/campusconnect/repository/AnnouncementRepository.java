package com.jay.campusconnect.repository;

import com.jay.campusconnect.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByEventId(Long eventId);
}
