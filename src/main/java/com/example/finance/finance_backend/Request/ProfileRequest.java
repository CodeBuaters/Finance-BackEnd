package com.example.finance.finance_backend.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

// Body for PUT /api/profile, e.g. { "firstName": "Anna", "lastName": "Smith" }
@Schema(description = "New first and last name")
public record ProfileRequest(@Schema(description = "First name", example = "Anna") @NotBlank String firstName, @Schema(description = "Last name", example = "Smith") @NotBlank String lastName) {
}
