package com.example.finance.finance_backend.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Totals across all of the user's transactions")
public class SummaryDto {

    @Schema(description = "Total income", example = "3200.00")
    private BigDecimal income;
    @Schema(description = "Total expenses", example = "2150.40")
    private BigDecimal expenses;
    @Schema(description = "Income minus expenses", example = "1049.60")
    private BigDecimal balance;
    @Schema(description = "Not implemented yet, always 0", example = "0")
    private BigDecimal savings;

    public SummaryDto(BigDecimal income, BigDecimal expenses, BigDecimal balance, BigDecimal savings) {

        this.income = income;
        this.expenses = expenses;
        this.balance = balance;
        this.savings = savings;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public BigDecimal getExpenses() {
        return expenses;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getSavings() {
        return savings;
    }
}
