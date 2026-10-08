package com.jay.campusconnect.dto.request;

import com.jay.campusconnect.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotNull(message = "Role is required")
        Role role,

        @NotNull(message = "Institution id is required")
        Long institutionId,

        String classOrYear,

        String department
) {
}
