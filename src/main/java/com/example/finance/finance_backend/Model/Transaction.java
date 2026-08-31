package com.example.finance.finance_backend.Model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate transactionDate;

    private String merchant;

    private String description;

    // TODO: TransactionType enum to categorize transactions (e.g., INCOME, EXPENSE)

    // TODO: Category enum to categorize transactions (e.g., FOOD, TRANSPORTATION,
    // ENTERTAINMENT)

    // TODO: User association to link transactions to specific users (e.g.,
    // ManyToOne relationship)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Transaction() {
    }
}
