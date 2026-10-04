package com.jay.campusconnect.mapper;

import com.jay.campusconnect.dto.request.InstitutionCreateRequest;
import com.jay.campusconnect.dto.request.InstitutionUpdateRequest;
import com.jay.campusconnect.dto.response.InstitutionResponse;
import com.jay.campusconnect.entity.Institution;
import org.springframework.stereotype.Component;

@Component
public class InstitutionMapper {

    public Institution toEntity(InstitutionCreateRequest request) {
        Institution institution = new Institution();
        institution.setName(request.name());
        institution.setType(request.type());
        institution.setCity(request.city());
        institution.setAddress(request.address());
        institution.setContactEmail(blankToNull(request.contactEmail()));
        institution.setPhone(request.phone());
        return institution;
    }

    public void updateEntity(Institution institution, InstitutionUpdateRequest request) {
        institution.setName(request.name());
        institution.setType(request.type());
        institution.setCity(request.city());
        institution.setAddress(request.address());
        institution.setContactEmail(blankToNull(request.contactEmail()));
        institution.setPhone(request.phone());
    }

    public InstitutionResponse toResponse(Institution institution) {
        return new InstitutionResponse(
                institution.getId(),
                institution.getName(),
                institution.getType(),
                institution.getCity(),
                institution.getAddress(),
                institution.getContactEmail(),
                institution.getPhone(),
                institution.getCreatedAt(),
                institution.getUpdatedAt()
        );
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }
}
