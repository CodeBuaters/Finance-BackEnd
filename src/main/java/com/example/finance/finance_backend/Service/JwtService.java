package com.example.finance.finance_backend.Service;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;

import com.example.finance.finance_backend.Dto.AccessTokenPayloadDto;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Service 
public class JwtService {
    
    @Value("${auth.jwt-secret}")
    private  String jwtSecret;

    private final String PREFIX = "Bearer ";


    public  AccessTokenPayloadDto getAccessToken(String username) {
        Instant expireAt = Instant.now().plus(8, ChronoUnit.HOURS);

        String accessToken = Jwts.builder()
            .setSubject(username)
            .setExpiration(Date.from(expireAt))
            .signWith(getSigninKey())
            .compact();

        return new AccessTokenPayloadDto(accessToken, expireAt);

    }

    public String getAuthUser(HttpServletRequest request) {
        String authorizationHeaderValue = 
            request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeaderValue == null) {
            return null;
        }

        try {
            String token = authorizationHeaderValue.replace(PREFIX, "");

            return getJwtParser()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
            
        } catch (Exception e) {
            return null;
        }
    }

    private SecretKey getSigninKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private JwtParser getJwtParser() {
        return Jwts.parser()
            .verifyWith(getSigninKey())
            .build();

    }
}
