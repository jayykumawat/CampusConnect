package com.jay.campusconnect.dto.response;

public record CsrfTokenResponse(
        String headerName,
        String parameterName,
        String token
) {
}
