package com.example.finance.finance_backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.finance.finance_backend.Model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.category = :category")
    Iterable<Transaction> findByCategory(@Param("category") String category);

    Iterable<Transaction> findByUserId(Long userId);

    Iterable<Transaction> findByTransactionType(String transactionType);
}
