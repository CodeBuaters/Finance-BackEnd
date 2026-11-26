package com.example.finance.finance_backend.Dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A family, its invite code and its members")
public record FamilyResponse(Long id, String name, String code, List<Member> members) {

    // Only the name, so members never see each other's email or password hash
    public record Member(Long id, String firstName, String lastName) {
    }
}
