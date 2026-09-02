package com.example.finance.finance_backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.finance.finance_backend.Model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
