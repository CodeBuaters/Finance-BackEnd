package com.example.finance.finance_backend.Dto;

public class CategoryResponse {

    private Long id;
    private String name;
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
