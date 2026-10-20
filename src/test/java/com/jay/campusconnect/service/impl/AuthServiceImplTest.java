package com.jay.campusconnect.service.impl;

import com.jay.campusconnect.dto.request.LoginRequest;
import com.jay.campusconnect.dto.request.PublicRegistrationRequest;
import com.jay.campusconnect.dto.request.UserCreateRequest;
import com.jay.campusconnect.dto.response.UserResponse;
import com.jay.campusconnect.enums.Role;
import com.jay.campusconnect.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void registrationAlwaysRequestsStudentRoleAndReturnsSafeFields() {
        PublicRegistrationRequest request = new PublicRegistrationRequest(
                "Asha", "asha@example.com", "correct-horse-7", 4L, "Year 1", "Science"
        );
        when(userService.registerStudent(org.mockito.ArgumentMatchers.any(UserCreateRequest.class)))
                .thenReturn(new UserResponse(
                        9L, "Asha", "asha@example.com", Role.STUDENT, null,
                        "Year 1", "Science", null, null
                ));

        var response = authService.register(request);

        ArgumentCaptor<UserCreateRequest> captor = ArgumentCaptor.forClass(UserCreateRequest.class);
        verify(userService).registerStudent(captor.capture());
        assertEquals(Role.STUDENT, captor.getValue().role());
        assertEquals(9L, response.id());
        assertEquals(Role.STUDENT, response.role());
    }

    @Test
    void authenticationUsesEmailAndRawSubmittedPasswordOnlyAsCredentials() {
        LoginRequest request = new LoginRequest("asha@example.com", "correct-horse-7");
        Authentication expected = UsernamePasswordAuthenticationToken.authenticated(
                "asha@example.com", null, java.util.List.of()
        );
        when(authenticationManager.authenticate(org.mockito.ArgumentMatchers.any(Authentication.class)))
                .thenReturn(expected);

        assertEquals(expected, authService.authenticate(request));

        ArgumentCaptor<Authentication> credentials = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(credentials.capture());
        assertEquals("asha@example.com", credentials.getValue().getName());
        assertEquals("correct-horse-7", credentials.getValue().getCredentials());
    }
}
