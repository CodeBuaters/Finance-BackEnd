package com.example.finance.finance_backend.Controller;

import com.example.finance.finance_backend.Service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final TransactionService transactionService;

    public PageController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("transactions", transactionService.getAllTransactions());
        return "index";
    }
}
