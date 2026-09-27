package com.example.finance.finance_backend.Dto;

import java.math.BigDecimal;

public class MonthlySpendingDto {

    private String month;
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
