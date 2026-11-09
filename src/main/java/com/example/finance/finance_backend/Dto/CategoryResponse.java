package com.example.finance.finance_backend.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A category available to the user")
public class CategoryResponse {

    @Schema(description = "Category ID", example = "3")
    private Long id;
    @Schema(description = "Category name", example = "Groceries")
    private String name;
    @Schema(description = "True for built-in categories shared by all users, false for the user's own")
    private boolean isDefault;

    public CategoryResponse(Long id, String name, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return isDefault;
    }

}
