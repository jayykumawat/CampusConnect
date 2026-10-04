package com.jay.campusconnect.service.impl;

import com.jay.campusconnect.dto.request.InstitutionCreateRequest;
import com.jay.campusconnect.dto.request.InstitutionUpdateRequest;
import com.jay.campusconnect.dto.response.InstitutionResponse;
import com.jay.campusconnect.entity.Institution;
import com.jay.campusconnect.exception.DuplicateResourceException;
import com.jay.campusconnect.exception.ResourceNotFoundException;
import com.jay.campusconnect.mapper.InstitutionMapper;
import com.jay.campusconnect.repository.InstitutionRepository;
import com.jay.campusconnect.service.InstitutionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final InstitutionMapper institutionMapper;

    public InstitutionServiceImpl(
            InstitutionRepository institutionRepository,
            InstitutionMapper institutionMapper
    ) {
        this.institutionRepository = institutionRepository;
        this.institutionMapper = institutionMapper;
    }

    @Override
    public InstitutionResponse createInstitution(InstitutionCreateRequest request) {
        if (institutionRepository.existsByNameAndCity(request.name(), request.city())) {
            throw new DuplicateResourceException(
                    "Institution already exists with name '" + request.name() + "' in city '" + request.city() + "'"
            );
        }
        assertContactEmailAvailable(request.contactEmail(), null);

        Institution institution = institutionMapper.toEntity(request);
        Institution saved = institutionRepository.save(institution);
        return institutionMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InstitutionResponse getInstitutionById(Long id) {
        return institutionMapper.toResponse(findInstitution(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstitutionResponse> getAllInstitutions() {
        return institutionRepository.findAll().stream()
                .map(institutionMapper::toResponse)
                .toList();
    }

    @Override
    public InstitutionResponse updateInstitution(Long id, InstitutionUpdateRequest request) {
        Institution institution = findInstitution(id);

        if (institutionRepository.existsByNameAndCityAndIdNot(request.name(), request.city(), id)) {
            throw new DuplicateResourceException(
                    "Institution already exists with name '" + request.name() + "' in city '" + request.city() + "'"
            );
        }
        assertContactEmailAvailable(request.contactEmail(), id);

        institutionMapper.updateEntity(institution, request);
        Institution saved = institutionRepository.save(institution);
        return institutionMapper.toResponse(saved);
    }

    @Override
    public void deleteInstitution(Long id) {
        if (!institutionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Institution not found with id " + id);
        }
        institutionRepository.deleteById(id);
    }

    private Institution findInstitution(Long id) {
        return institutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id " + id));
    }

    private void assertContactEmailAvailable(String contactEmail, Long currentId) {
        if (contactEmail == null || contactEmail.isBlank()) {
            return;
        }
        boolean duplicate = currentId == null
                ? institutionRepository.existsByContactEmail(contactEmail)
                : institutionRepository.existsByContactEmailAndIdNot(contactEmail, currentId);
        if (duplicate) {
            throw new DuplicateResourceException(
                    "Institution already exists with contact email '" + contactEmail + "'"
            );
        }
    }
}
