package com.example.finance.finance_backend.Request;

import jakarta.validation.constraints.NotBlank;

// Body for PUT /api/profile, e.g. { "firstName": "Anna", "lastName": "Smith" }
public record ProfileRequest(@NotBlank String firstName, @NotBlank String lastName) {
}
