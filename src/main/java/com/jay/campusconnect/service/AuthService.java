package com.jay.campusconnect.service;

import com.jay.campusconnect.dto.request.LoginRequest;
import com.jay.campusconnect.dto.request.PublicRegistrationRequest;
import com.jay.campusconnect.dto.response.AuthResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {

    AuthResponse register(PublicRegistrationRequest request);

    Authentication authenticate(LoginRequest request);
}
