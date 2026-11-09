package com.example.finance.finance_backend.Request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "New category for a transaction")
public class TransactionCategoryRequest {

    @Schema(description = "ID of a default category or one of the user's own", example = "3")
    private Long categoryId;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
