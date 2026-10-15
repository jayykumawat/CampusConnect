package com.jay.campusconnect.controller;

import com.jay.campusconnect.dto.request.RegistrationCreateRequest;
import com.jay.campusconnect.dto.response.RegistrationResponse;
import com.jay.campusconnect.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<RegistrationResponse> create(
            @Valid @RequestBody RegistrationCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationService.createRegistration(request));
    }

    @GetMapping("/{id}")
    public RegistrationResponse getById(@PathVariable Long id) {
        return registrationService.getRegistrationById(id);
    }

    @GetMapping("/event/{eventId}")
    public List<RegistrationResponse> getByEventId(@PathVariable Long eventId) {
        return registrationService.getRegistrationsByEventId(eventId);
    }

    @GetMapping("/user/{userId}")
    public List<RegistrationResponse> getByUserId(@PathVariable Long userId) {
        return registrationService.getRegistrationsByUserId(userId);
    }

    @PostMapping("/{id}/cancel")
    public RegistrationResponse cancel(@PathVariable Long id) {
        return registrationService.cancelRegistration(id);
    }
}
