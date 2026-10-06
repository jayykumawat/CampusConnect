package com.jay.campusconnect.controller;

import com.jay.campusconnect.dto.request.InstitutionCreateRequest;
import com.jay.campusconnect.dto.request.InstitutionUpdateRequest;
import com.jay.campusconnect.dto.response.InstitutionResponse;
import com.jay.campusconnect.service.InstitutionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/institutions")
public class InstitutionController {

    private final InstitutionService institutionService;

    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    @PostMapping
    public ResponseEntity<InstitutionResponse> create(
            @Valid @RequestBody InstitutionCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(institutionService.createInstitution(request));
    }

    @GetMapping("/{id}")
    public InstitutionResponse getById(@PathVariable Long id) {
        return institutionService.getInstitutionById(id);
    }

    @GetMapping
    public List<InstitutionResponse> getAll() {
        return institutionService.getAllInstitutions();
    }

    @PutMapping("/{id}")
    public InstitutionResponse update(
            @PathVariable Long id,
            @Valid @RequestBody InstitutionUpdateRequest request
    ) {
        return institutionService.updateInstitution(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        institutionService.deleteInstitution(id);
    }
}
