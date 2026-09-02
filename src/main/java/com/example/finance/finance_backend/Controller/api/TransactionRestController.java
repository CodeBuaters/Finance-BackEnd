package com.example.finance.finance_backend.Controller.api;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Repository.TransactionRepository;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class TransactionRestController {

    private final TransactionRepository transactionRepository;

    public TransactionRestController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/api/transactions")
    public @ResponseBody Iterable<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @GetMapping("/api/transactions/{id}")
    public @ResponseBody Transaction getTransactionById(@PathVariable Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    @GetMapping("/api/category/{category}/transactions")
    public @ResponseBody Iterable<Transaction> getTransactionsByCategory(@PathVariable String category) {
        return transactionRepository.findByCategory(category);
    }

    @GetMapping("/api/user/{userId}/transactions")
    public @ResponseBody Iterable<Transaction> getTransactionsByUserId(@PathVariable Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    @GetMapping("/api/transaction-type/{transactionType}/transactions")
    public @ResponseBody Iterable<Transaction> getTransactionsByTransactionType(@PathVariable String transactionType) {
        return transactionRepository.findByTransactionType(transactionType);
    }
}
