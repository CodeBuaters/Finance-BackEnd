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
    SALARY("Salary"),
    TRAVEL("Travel"),
    SHOPPING("Shopping"),
    TRANSFER("Transfer"),
    OTHER("Other");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
