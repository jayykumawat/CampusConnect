package com.jay.campusconnect.repository;

import com.jay.campusconnect.entity.Event;
import com.jay.campusconnect.entity.Institution;
import com.jay.campusconnect.enums.EventCategory;
import com.jay.campusconnect.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {
    List<Event> findByStatus(EventStatus status);
    List<Event> findByInstitution(Institution institution);

    List<Event> findByInstitutionAndStatus(
            Institution institution,
            EventStatus status
    );

    List<Event> findByCategory(EventCategory category);

    List<Event> findByDateTimeAfterOrderByDateTimeAsc(
            LocalDateTime dateTime
    );
}
