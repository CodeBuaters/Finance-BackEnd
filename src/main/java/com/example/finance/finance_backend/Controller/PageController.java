package com.example.finance.finance_backend.Controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.finance.finance_backend.Model.Category;
import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Model.TransactionType;
import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Repository.UserRepository;
import com.example.finance.finance_backend.Request.RegisterRequest;
import com.example.finance.finance_backend.Service.TransactionService;

@Controller
public class PageController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public PageController(TransactionService transactionService, UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request, Model model) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()
                || userRepository.findByEmail(request.getEmail()).isPresent()) {
            model.addAttribute("error", "Username or email already exists.");
            return "register";
        }

        User user = new User(request.getUsername(), request.getFirstName(), request.getLastName(),
                request.getEmail(), passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/transaction/new")
    public String newTransaction(Model model) {
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("transactionTypes", TransactionType.values());
        model.addAttribute("categories", Category.values());
        return "transactions/form";
    }

    @GetMapping("/transaction/edit/{id}")
    public String editTransaction(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        return transactionService.getTransactionById(id)
                .map(transaction -> {
                    model.addAttribute("transaction", transaction);
                    model.addAttribute("transactionTypes", TransactionType.values());
                    model.addAttribute("categories", Category.values());
                    return "transactions/form";
                })
                .orElse("redirect:/transactions");
    }

    @GetMapping("/transactions")
    public String transactions(Model model) {
        model.addAttribute("transactions", transactionService.getAllTransactions());
        return "transactions/list";
    }

    @GetMapping("/transactions/import")
    public String importTransactions() {
        return "transactions/import";
    }
}
