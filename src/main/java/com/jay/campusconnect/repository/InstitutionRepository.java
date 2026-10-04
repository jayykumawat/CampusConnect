package com.jay.campusconnect.repository;

import com.jay.campusconnect.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    boolean existsByNameAndCity(String name, String city);

    boolean existsByContactEmail(String contactEmail);

    boolean existsByNameAndCityAndIdNot(String name, String city, Long id);

    boolean existsByContactEmailAndIdNot(String contactEmail, Long id);
}
