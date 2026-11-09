package com.example.finance.finance_backend.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Total expenses in one month")
public class MonthlySpendingDto {

    @Schema(description = "Month in format YYYY-MM", example = "2026-10")
    private String month;
    @Schema(description = "Total expenses that month", example = "1830.25")
    private BigDecimal total;

    public MonthlySpendingDto(String month, BigDecimal total) {
        this.month = month;
        this.total = total;
    }

    public String getMonth() {
        return month;
    }

    public BigDecimal getTotal() {
        return total;
    }

}
