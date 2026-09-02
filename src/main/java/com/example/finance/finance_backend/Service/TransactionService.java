package com.example.finance.finance_backend.Service;

import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public Iterable<Transaction> getTransactionsByCategory(String category) {
        return transactionRepository.findByCategory(category);
    }

    public Iterable<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
}
