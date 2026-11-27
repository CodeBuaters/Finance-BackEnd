package com.example.finance.finance_backend.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;

// Body for POST /api/family { "name": "Smith family" } and POST /api/family/join { "code": "K7P2XQ9M" }
@Schema(description = "Family name (to create) or invite code (to join)")
public record FamilyRequest(
        @Schema(description = "Name of the new family", example = "Smith family") @Size(max = 100) String name,
        @Schema(description = "Invite code of an existing family", example = "K7P2XQ9M") String code) {
}
