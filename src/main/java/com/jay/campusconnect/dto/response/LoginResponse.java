package com.jay.campusconnect.dto.response;

public record LoginResponse(
        boolean authenticated,
        String email
) {
}
