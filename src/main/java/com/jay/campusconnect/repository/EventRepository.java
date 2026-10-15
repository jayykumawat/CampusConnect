package com.jay.campusconnect.repository;

import com.jay.campusconnect.entity.Event;
import com.jay.campusconnect.entity.Institution;
import com.jay.campusconnect.enums.EventCategory;
import com.jay.campusconnect.enums.EventStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Event e where e.id = :id")
    Optional<Event> findByIdForUpdate(@Param("id") Long id);

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
