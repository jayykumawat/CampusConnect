package com.jay.campusconnect.service;

import com.jay.campusconnect.dto.request.InstitutionCreateRequest;
import com.jay.campusconnect.dto.request.InstitutionUpdateRequest;
import com.jay.campusconnect.dto.response.InstitutionResponse;

import java.util.List;

public interface InstitutionService {

    InstitutionResponse createInstitution(InstitutionCreateRequest request);

    InstitutionResponse getInstitutionById(Long id);

    List<InstitutionResponse> getAllInstitutions();

    InstitutionResponse updateInstitution(Long id, InstitutionUpdateRequest request);

    void deleteInstitution(Long id);
}
