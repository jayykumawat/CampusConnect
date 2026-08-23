package com.jay.campusconnect.repository;

import com.jay.campusconnect.entity.Event;
import com.jay.campusconnect.entity.Registration;
import com.jay.campusconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration,Long> {

    Optional<Registration> findByEventAndUser(
            Event event,
            User user
    );

    boolean existsByEventAndUser(
            Event event,
            User user
    );

    List<Registration> findByEvent(Event event);

    List<Registration> findByUser(User user);
}
