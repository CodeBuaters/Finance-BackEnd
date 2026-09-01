package com.example.finance.finance_backend.Controller;

import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Model.TransactionType;
import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Repository.UserRepository;
import com.example.finance.finance_backend.Service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public TransactionController(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    @PostMapping("/transaction")
    public String save(@RequestParam BigDecimal amount,
                       @RequestParam String merchant,
                       @RequestParam(required = false) String description,
                       @RequestParam TransactionType transactionType) {
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
        t.setTransactionDate(LocalDate.now());
        t.setUser(user);
        transactionService.saveTransaction(t);
        return "redirect:/";
    }

    @PostMapping("/transaction/delete/{id}")
    public String delete(@PathVariable Long id) {
        transactionService.getTransactionById(id).ifPresent(transaction -> transactionService.deleteTransaction(id));
        return "redirect:/";
    }
}

