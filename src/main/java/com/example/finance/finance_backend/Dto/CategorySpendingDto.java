package com.example.finance.finance_backend.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

import com.example.finance.finance_backend.Model.Category;

@Schema(description = "Total expenses in one category")
public class CategorySpendingDto {

    @Schema(description = "The category")
    private Category category;
    @Schema(description = "Total expenses in this category", example = "412.90")
    private BigDecimal total;

    public CategorySpendingDto(Category category, BigDecimal total) {
        this.category = category;
        this.total = total;
    }

    public Category getCategory() {
        return category;
    }

    public BigDecimal getTotal() {
        return total;
    }

}
