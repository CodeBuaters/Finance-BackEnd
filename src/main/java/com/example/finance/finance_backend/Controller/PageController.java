package com.example.finance.finance_backend.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.finance.finance_backend.Model.Category;
import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Model.TransactionType;
import com.example.finance.finance_backend.Service.TransactionService;

@Controller
public class PageController {

    private final TransactionService transactionService;

    public PageController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
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
}
