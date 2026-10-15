package com.jay.campusconnect.service;

import com.jay.campusconnect.dto.request.RegistrationCreateRequest;
import com.jay.campusconnect.dto.response.RegistrationResponse;

import java.util.List;

public interface RegistrationService {

    RegistrationResponse createRegistration(RegistrationCreateRequest request);

    RegistrationResponse getRegistrationById(Long id);

    List<RegistrationResponse> getRegistrationsByEventId(Long eventId);

    List<RegistrationResponse> getRegistrationsByUserId(Long userId);

    RegistrationResponse cancelRegistration(Long id);
}
