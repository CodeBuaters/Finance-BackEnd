package com.example.finance.finance_backend.Request;
import com.example.finance.finance_backend.Model.TransactionType;
public class TransactionRequest {
    private String description;
    private TransactionType transactionType;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}
