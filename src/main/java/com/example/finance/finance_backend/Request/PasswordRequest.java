package com.example.finance.finance_backend.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Body for PUT /api/profile/password. The new password must have at least 8 characters.
@Schema(description = "Current and new password")
public record PasswordRequest(@Schema(description = "The user's current password", example = "secret123") @NotBlank String currentPassword, @Schema(description = "New password, at least 8 characters", example = "newSecret456") @NotBlank @Size(min = 8) String newPassword) {
}
