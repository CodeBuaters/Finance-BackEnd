package com.example.finance.finance_backend.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

import com.example.finance.finance_backend.Model.Category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Schema(description = "Monthly spending limit for one category")
public class BudgetRequest {

    @NotNull(message = "Category is required")
    @Schema(description = "Category the budget applies to; only the id is needed")
    private Category category;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    @Schema(description = "Spending limit in euros, greater than 0", example = "300.00")
    private BigDecimal amount;

    @NotNull(message = "Month is required")
    @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])", message = "Month must be in format YYYY-MM")
    @Schema(description = "Month in format YYYY-MM", example = "2026-11")
    private String month;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
