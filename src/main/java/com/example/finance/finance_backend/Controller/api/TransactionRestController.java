package com.example.finance.finance_backend.Controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@Tag(name = "Transactions", description = "View bank transactions, import them from CSV and change their category")
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

    @Operation(summary = "List all transactions", description = "Returns every transaction in the database. Not filtered by user; use GET /api/user/{userId}/transactions for the logged in user's own transactions.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of transactions"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @GetMapping("/api/transactions")
    public @ResponseBody Iterable<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Operation(summary = "Get a transaction", description = "Returns one transaction by ID. The response body is empty if no transaction has that ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The transaction, or an empty body if not found"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @GetMapping("/api/transactions/{id}")
    public @ResponseBody Transaction getTransactionById(
            @Parameter(description = "ID of the transaction", example = "1") @PathVariable Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    @Operation(summary = "List transactions in a category", description = "Returns all transactions that belong to the given category. Returns an empty list if the category does not exist.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of transactions"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @GetMapping("/api/category/{categoryId}/transactions")
    public @ResponseBody Iterable<Transaction> getTransactionsByCategory(
            @Parameter(description = "ID of the category", example = "3") @PathVariable Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElse(null);

        if (category == null) {
            return java.util.List.of();
        }

        return transactionRepository.findByCategory(category);
    }

    @Operation(summary = "List my transactions", description = "Returns all transactions of the logged in user. The userId must match the user in the JWT token; users cannot read each other's transactions.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of transactions"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content),
            @ApiResponse(responseCode = "500", description = "userId does not match the logged in user", content = @Content)
    })
    @GetMapping("/api/user/{userId}/transactions")
    public @ResponseBody Iterable<Transaction> getTransactionsByUserId(
            @Parameter(description = "ID of the logged in user", example = "1") @PathVariable Long userId,
            HttpServletRequest request) {
        return transactionService.getTransactionsForCurrentUser(request, userId);
    }

    @Operation(summary = "List transactions by type", description = "Returns all transactions of the given type, for example only income or only expenses.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of transactions"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @GetMapping("/api/transaction-type/{transactionType}/transactions")
    public @ResponseBody Iterable<Transaction> getTransactionsByTransactionType(
            @Parameter(description = "Transaction type to filter by") @PathVariable TransactionType transactionType) {
        return transactionRepository.findByTransactionType(transactionType);
    }

    @Operation(summary = "Import transactions from CSV", description = "Uploads a bank statement as a .csv file. Each row is saved as a transaction and categorized automatically from its merchant name. Returns the saved transactions.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions imported"),
            @ApiResponse(responseCode = "400", description = "File is empty, not a .csv file, or has an invalid row (bad date, amount, type or missing column)"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @PostMapping("/api/import")
    public ResponseEntity<?> importTransactions(
            @Parameter(description = "CSV file with a header row. Required columns: amount, merchant. Optional: date (YYYY-MM-DD), description, type (INCOME, EXPENSE or TRANSFER), category")
            @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(csvImportService.parseCsv(file));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @Operation(summary = "Change a transaction's category", description = "Moves a transaction of the logged in user to another category. The category must be a default category or one of the user's own.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction updated"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content),
            @ApiResponse(responseCode = "500", description = "Transaction or category belongs to another user", content = @Content)
    })
    @PutMapping("/api/transactions/{transactionId}/category")
    public Transaction updateTransactionCategory(
            @Parameter(description = "ID of the transaction to recategorize", example = "1") @PathVariable Long transactionId,
            @RequestBody TransactionCategoryRequest categoryRequest, HttpServletRequest request) {
        return transactionService.updateTransactionCategory(transactionId, categoryRequest.getCategoryId(), request);
    }

}
