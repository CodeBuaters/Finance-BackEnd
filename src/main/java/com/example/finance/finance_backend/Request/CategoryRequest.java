package com.example.finance.finance_backend.Request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Name of a custom category")
public class CategoryRequest {

    @Schema(description = "Category name", example = "Pets")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
