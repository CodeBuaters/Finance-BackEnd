package com.example.finance.finance_backend.Model;

public enum Category {
    FOOD("Food"),
    TRANSPORTATION("Transportation"),
    ENTERTAINMENT("Entertainment"),
    HOUSING("Housing"),
    UTILITIES("Utilities"),
    HEALTHCARE("Healthcare"),
    EDUCATION("Education"),
    PERSONAL_CARE("Personal Care"),
    TRAVEL("Travel"),
    SHOPPING("Shopping"),
    OTHER("Other"),
    SALARY("Salary"),
    TRANSFER("Transfer");

    

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
