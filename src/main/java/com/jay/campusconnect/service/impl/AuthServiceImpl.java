package com.jay.campusconnect.service.impl;

import com.jay.campusconnect.dto.request.LoginRequest;
import com.jay.campusconnect.dto.request.PublicRegistrationRequest;
import com.jay.campusconnect.dto.request.UserCreateRequest;
import com.jay.campusconnect.dto.response.AuthResponse;
import com.jay.campusconnect.dto.response.UserResponse;
import com.jay.campusconnect.enums.Role;
import com.jay.campusconnect.service.AuthService;
import com.jay.campusconnect.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(
            UserService userService,
            AuthenticationManager authenticationManager
    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public AuthResponse register(PublicRegistrationRequest request) {
        UserCreateRequest studentRequest = new UserCreateRequest(
                request.name(),
                request.email(),
                request.password(),
                Role.STUDENT,
                request.institutionId(),
                request.classOrYear(),
                request.department()
        );
        UserResponse user = userService.registerStudent(studentRequest);
        return new AuthResponse(user.id(), user.name(), user.email(), user.role());
    }

    @Override
    public Authentication authenticate(LoginRequest request) {
        return authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.email(),
                        request.password()
                )
        );
    }
}
