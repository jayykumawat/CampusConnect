package com.jay.campusconnect.service.impl;

import com.jay.campusconnect.dto.request.UserCreateRequest;
import com.jay.campusconnect.dto.response.UserResponse;
import com.jay.campusconnect.entity.Institution;
import com.jay.campusconnect.entity.User;
import com.jay.campusconnect.enums.Role;
import com.jay.campusconnect.exception.DuplicateResourceException;
import com.jay.campusconnect.mapper.UserMapper;
import com.jay.campusconnect.repository.InstitutionRepository;
import com.jay.campusconnect.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegistrationSecurityTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private InstitutionRepository institutionRepository;
    @Mock
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userRepository,
                institutionRepository,
                userMapper,
                passwordEncoder
        );
    }

    @Test
    void publicRegistrationForcesStudentAndStoresBcryptHash() {
        UserCreateRequest maliciousRequest = new UserCreateRequest(
                "Asha", "asha@example.com", "correct-horse-7", Role.SUPER_ADMIN,
                4L, "Year 1", "Science"
        );
        Institution institution = new Institution();
        User user = new User();
        when(userRepository.existsByEmail(maliciousRequest.email())).thenReturn(false);
        when(institutionRepository.findById(4L)).thenReturn(Optional.of(institution));
        when(userMapper.toEntity(any(UserCreateRequest.class), any(Institution.class)))
                .thenAnswer(invocation -> {
                    UserCreateRequest mapperRequest = invocation.getArgument(0);
                    user.setName(mapperRequest.name());
                    user.setEmail(mapperRequest.email());
                    user.setRole(mapperRequest.role());
                    user.setInstitution(institution);
                    return user;
                });
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(new UserResponse(
                9L, "Asha", "asha@example.com", Role.STUDENT, null,
                "Year 1", "Science", null, null
        ));

        UserResponse response = userService.registerStudent(maliciousRequest);

        ArgumentCaptor<UserCreateRequest> mappedRequest =
                ArgumentCaptor.forClass(UserCreateRequest.class);
        verify(userMapper).toEntity(mappedRequest.capture(), any(Institution.class));
        assertEquals(Role.STUDENT, mappedRequest.getValue().role());
        assertEquals(Role.STUDENT, user.getRole());
        assertNotEquals(maliciousRequest.password(), user.getPasswordHash());
        assertTrue(passwordEncoder.matches(maliciousRequest.password(), user.getPasswordHash()));
        assertEquals(Role.STUDENT, response.role());
    }

    @Test
    void duplicateEmailIsRejected() {
        UserCreateRequest request = new UserCreateRequest(
                "Asha", "asha@example.com", "correct-horse-7", Role.ADMIN,
                4L, null, null
        );
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.registerStudent(request));
    }
}
