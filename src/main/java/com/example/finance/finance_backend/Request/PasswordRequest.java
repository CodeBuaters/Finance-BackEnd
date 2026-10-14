package com.example.finance.finance_backend.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Body for PUT /api/profile/password. The new password must have at least 8 characters.
public record PasswordRequest(@NotBlank String currentPassword, @NotBlank @Size(min = 8) String newPassword) {
}
