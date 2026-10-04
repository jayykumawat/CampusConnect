package com.jay.campusconnect.dto.request;

import com.jay.campusconnect.enums.InstitutionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InstitutionCreateRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Type is required")
        InstitutionType type,

        @NotBlank(message = "City is required")
        String city,

        String address,

        @Email(message = "Contact email must be a valid email address")
        String contactEmail,

        String phone
) {
}
