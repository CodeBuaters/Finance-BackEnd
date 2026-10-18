package com.example.finance.finance_backend.Controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.finance.finance_backend.Model.Category;
import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Model.TransactionType;
import com.example.finance.finance_backend.Repository.CategoryRepository;
import com.example.finance.finance_backend.Repository.TransactionRepository;
import com.example.finance.finance_backend.Request.TransactionCategoryRequest;
import com.example.finance.finance_backend.Service.CsvImportService;
import com.example.finance.finance_backend.Service.TransactionService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class TransactionRestController {

    private final TransactionRepository transactionRepository;
    private final CsvImportService csvImportService;
    private final CategoryRepository categoryRepository;
    private final TransactionService transactionService;

    public TransactionRestController(TransactionRepository transactionRepository, CsvImportService csvImportService,
            CategoryRepository categoryRepository, TransactionService transactionService) {
        this.transactionRepository = transactionRepository;
        this.csvImportService = csvImportService;
        this.categoryRepository = categoryRepository;
        this.transactionService = transactionService;
    }

    @GetMapping("/api/transactions")
    public @ResponseBody Iterable<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @GetMapping("/api/transactions/{id}")
    public @ResponseBody Transaction getTransactionById(@PathVariable Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    @GetMapping("/api/category/{categoryId}/transactions")
    public @ResponseBody Iterable<Transaction> getTransactionsByCategory(@PathVariable Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElse(null);

        if (category == null) {
            return java.util.List.of();
        }

        return transactionRepository.findByCategory(category);
    }

    @GetMapping("/api/user/{userId}/transactions")
    public @ResponseBody Iterable<Transaction> getTransactionsByUserId(@PathVariable Long userId,
            HttpServletRequest request) {
        return transactionService.getTransactionsForCurrentUser(request, userId);
    }

    @GetMapping("/api/transaction-type/{transactionType}/transactions")
    public @ResponseBody Iterable<Transaction> getTransactionsByTransactionType(
            @PathVariable TransactionType transactionType) {
        return transactionRepository.findByTransactionType(transactionType);
    }

    @PostMapping("/api/import")
    public ResponseEntity<?> importTransactions(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(csvImportService.parseCsv(file));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PutMapping("/api/transactions/{transactionId}/category")
    public Transaction updateTransactionCategory(@PathVariable Long transactionId,
            @RequestBody TransactionCategoryRequest categoryRequest, HttpServletRequest request) {
        return transactionService.updateTransactionCategory(transactionId, categoryRequest.getCategoryId(), request);
    }

}
