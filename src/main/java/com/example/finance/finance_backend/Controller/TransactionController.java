package com.example.finance.finance_backend.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.finance.finance_backend.Model.Category;
import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Model.TransactionType;
import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Repository.UserRepository;
import com.example.finance.finance_backend.Service.TransactionService;
import com.example.finance.finance_backend.Service.CsvImportService;

@Controller
public class TransactionController {

    private final TransactionService transactionService;
    private final CsvImportService csvImportService;
    private final UserRepository userRepository;

    public TransactionController(TransactionService transactionService, CsvImportService csvImportService,
            UserRepository userRepository) {
        this.transactionService = transactionService;
        this.csvImportService = csvImportService;
        this.userRepository = userRepository;
    }

    @PostMapping("/transaction/new")
    public String save(@RequestParam BigDecimal amount,
            @RequestParam String merchant,
            @RequestParam(required = false) String description,
            @RequestParam TransactionType transactionType,
            @RequestParam(required = false) Category category) {
        User user = userRepository.findByUsername("demo").orElseGet(() -> {
            User u = new User();
            u.setUsername("demo");
            u.setFirstName("Demo");
            u.setLastName("User");
            u.setEmail("demo@example.com");
            u.setPasswordHash("demo-pass");
            return userRepository.save(u);
        });

        Transaction t = new Transaction();
        t.setAmount(amount);
        t.setMerchant(merchant);
        t.setDescription(description == null || description.isBlank() ? "No description" : description);
        t.setTransactionType(transactionType);
        t.setCategory(category);
        t.setTransactionDate(LocalDate.now());
        t.setUser(user);
        transactionService.saveTransaction(t);
        return "redirect:/transactions";
    }

    // EDIT TRANSACTION
    @PostMapping("/transaction/edit/{id}")
    public String edit(@PathVariable Long id, @RequestParam BigDecimal amount, @RequestParam String merchant,
            @RequestParam(required = false) String description, @RequestParam TransactionType transactionType,
            @RequestParam(required = false) Category category) {
        transactionService.getTransactionById(id).ifPresent(transaction -> {
            transaction.setAmount(amount);
            transaction.setMerchant(merchant);
            transaction.setDescription(description == null || description.isBlank() ? "No description" : description);
            transaction.setTransactionType(transactionType);
            transaction.setCategory(category);
            transactionService.saveTransaction(transaction);
        });
        return "redirect:/transactions";
    }

    // DELETE TRANSACTION
    @PostMapping("/transaction/delete/{id}")
    public String delete(@PathVariable Long id) {
        transactionService.getTransactionById(id).ifPresent(transaction -> transactionService.deleteTransaction(id));
        return "redirect:/transactions";
    }

    // FILE UPLOAD
    @PostMapping("/import")
    public ResponseEntity<String> importTransactions(@RequestParam("file") MultipartFile file) {
        try {
            User user = userRepository.findByUsername("demo").orElseGet(() -> {
                User newUser = new User();
                newUser.setUsername("demo");
                newUser.setFirstName("Demo");
                newUser.setLastName("User");
                newUser.setEmail("demo@example.com");
                newUser.setPasswordHash("demo-pass");
                return userRepository.save(newUser);
            });

            int imported = 0;
            for (Transaction transaction : csvImportService.parseCsv(file)) {
                transaction.setUser(user);
                transactionService.saveTransaction(transaction);
                imported++;
            }
            return ResponseEntity.ok(imported + " transactions imported successfully.");
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}
