package com.example.finance.finance_backend.Dto;

import java.math.BigDecimal;

import com.example.finance.finance_backend.Model.Category;

public class CategorySpendingDto {

    private Category category;
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
