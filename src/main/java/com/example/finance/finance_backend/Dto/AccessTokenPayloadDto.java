package com.example.finance.finance_backend.Dto;

import java.time.Instant;

public class AccessTokenPayloadDto {
    
    private String accessToken;
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
