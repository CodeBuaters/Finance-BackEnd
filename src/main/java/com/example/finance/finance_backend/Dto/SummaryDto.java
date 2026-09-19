package com.example.finance.finance_backend.Dto;

import java.math.BigDecimal;

public class SummaryDto {

    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal balance;
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
