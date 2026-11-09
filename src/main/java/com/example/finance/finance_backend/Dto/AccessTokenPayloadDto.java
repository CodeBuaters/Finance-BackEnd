package com.example.finance.finance_backend.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "JWT access token returned on login")
public class AccessTokenPayloadDto {
    
    @Schema(description = "Signed JWT. Send it as 'Authorization: Bearer <token>'", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
    @Schema(description = "When the token expires (UTC)", example = "2026-11-10T12:00:00Z")
    private Instant expiresAt;

    public  AccessTokenPayloadDto(String accessToken, Instant expiresAt) {
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
    

}
