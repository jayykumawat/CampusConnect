package com.jay.campusconnect.controller;

import com.jay.campusconnect.config.PasswordEncoderConfig;
import com.jay.campusconnect.config.SecurityConfig;
import com.jay.campusconnect.dto.request.LoginRequest;
import com.jay.campusconnect.dto.request.PublicRegistrationRequest;
import com.jay.campusconnect.dto.response.AuthResponse;
import com.jay.campusconnect.enums.Role;
import com.jay.campusconnect.exception.DuplicateResourceException;
import com.jay.campusconnect.exception.GlobalExceptionHandler;
import com.jay.campusconnect.security.CampusConnectUserDetailsService;
import com.jay.campusconnect.service.AuthService;
import com.jay.campusconnect.service.EventService;
import com.jay.campusconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, EventController.class, UserController.class})
@Import({SecurityConfig.class, PasswordEncoderConfig.class, GlobalExceptionHandler.class})
class AuthControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private EventService eventService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private CampusConnectUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        when(eventService.getEventsByFilters(null, null)).thenReturn(List.of());
        when(userService.getAllUsers()).thenReturn(List.of());
        when(userDetailsService.loadUserByUsername("asha@example.com"))
                .thenReturn(User.withUsername("asha@example.com")
                        .password(passwordEncoder.encode("correct-horse-7"))
                        .roles("STUDENT")
                        .build());
        when(authService.authenticate(any(LoginRequest.class))).thenAnswer(invocation -> {
            LoginRequest request = invocation.getArgument(0);
            return authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.email(),
                            request.password()
                    )
            );
        });
    }

    @Test
    void publicRegistrationIgnoresSubmittedPrivilegedRoleAndReturnsSafeStudentResponse()
            throws Exception {
        when(authService.register(any(PublicRegistrationRequest.class)))
                .thenReturn(new AuthResponse(9L, "Asha", "asha@example.com", Role.STUDENT));

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Asha",
                                  "email":"asha@example.com",
                                  "password":"correct-horse-7",
                                  "institutionId":4,
                                  "role":"SUPER_ADMIN"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void registrationValidationReturnsClearBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"",
                                  "email":"not-an-email",
                                  "password":"short",
                                  "institutionId":0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    void duplicateRegistrationReturnsConflict() throws Exception {
        when(authService.register(any(PublicRegistrationRequest.class)))
                .thenThrow(new DuplicateResourceException("User already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Asha",
                                  "email":"asha@example.com",
                                  "password":"correct-horse-7",
                                  "institutionId":4
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User already exists"));
    }

    @Test
    void csrfEndpointReturnsHeaderNameAndToken() throws Exception {
        mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headerName").value("X-CSRF-TOKEN"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void csrfTokenRetrievedFromEndpointAllowsPublicRegistration() throws Exception {
        when(authService.register(any(PublicRegistrationRequest.class)))
                .thenReturn(new AuthResponse(9L, "Asha", "asha@example.com", Role.STUDENT));
        var csrfResult = mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpSession session = (MockHttpSession) csrfResult.getRequest().getSession(false);
        assertNotNull(session);
        Matcher tokenMatcher = Pattern.compile("\"token\":\"([^\"]+)\"")
                .matcher(csrfResult.getResponse().getContentAsString());
        assertTrue(tokenMatcher.find());

        mockMvc.perform(post("/api/auth/register")
                        .session(session)
                        .header("X-CSRF-TOKEN", tokenMatcher.group(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Asha",
                                  "email":"asha@example.com",
                                  "password":"correct-horse-7",
                                  "institutionId":4
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void loginRequiresCsrfAndEstablishesSessionForValidCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"asha@example.com","password":"correct-horse-7"}
                                """))
                .andExpect(status().isForbidden());

        var csrfResult = mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpSession anonymousSession = (MockHttpSession) csrfResult.getRequest().getSession(false);
        assertNotNull(anonymousSession);
        String originalSessionId = anonymousSession.getId();
        Matcher tokenMatcher = Pattern.compile("\"token\":\"([^\"]+)\"")
                .matcher(csrfResult.getResponse().getContentAsString());
        assertTrue(tokenMatcher.find());

        var loginResult = mockMvc.perform(post("/api/auth/login")
                                .session(anonymousSession)
                                .header("X-CSRF-TOKEN", tokenMatcher.group(1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"email":"asha@example.com","password":"correct-horse-7"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertNotNull(session);
        assertNotEquals(originalSessionId, session.getId());
        assertNotNull(session.getAttribute("SPRING_SECURITY_CONTEXT"));

        mockMvc.perform(get("/api/events").session(session))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidCredentialsReturnGenericFailure() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"asha@example.com","password":"wrong-password"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void unknownEmailReturnsSameGenericFailure() throws Exception {
        when(userDetailsService.loadUserByUsername("missing@example.com"))
                .thenThrow(new UsernameNotFoundException("No matching user"));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"missing@example.com","password":"wrong-password"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void unauthenticatedProtectedEndpointIsDeniedWithJson() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void logoutInvalidatesAuthenticatedSession() throws Exception {
        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"asha@example.com","password":"correct-horse-7"}
                                """))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertNotNull(session);

        mockMvc.perform(post("/api/auth/logout")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertTrue(session.isInvalid());
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isUnauthorized());
    }
}
