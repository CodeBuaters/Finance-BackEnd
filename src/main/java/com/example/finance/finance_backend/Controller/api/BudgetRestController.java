package com.example.finance.finance_backend.Controller.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.finance.finance_backend.Model.Budget;
import com.example.finance.finance_backend.Request.BudgetRequest;
import com.example.finance.finance_backend.Service.BudgetService;

import jakarta.validation.Valid;

@RestController
public class BudgetRestController {

    private final BudgetService budgetService;

    public BudgetRestController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // authentication.getName() is the logged in user's email

    @GetMapping("/api/budgets")
    public List<Budget> getBudgets(Authentication authentication) {
        return budgetService.getBudgets(authentication.getName());
    }

    @GetMapping("/api/budgets/{id}")
    public Budget getBudget(Authentication authentication, @PathVariable Long id) {
        return budgetService.getBudget(authentication.getName(), id);
    }

    @PostMapping("/api/budgets")
    public ResponseEntity<Budget> createBudget(Authentication authentication,
            @Valid @RequestBody BudgetRequest request) {
        Budget budget = budgetService.createBudget(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(budget);
    }

    @PutMapping("/api/budgets/{id}")
    public Budget updateBudget(Authentication authentication, @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {
        return budgetService.updateBudget(authentication.getName(), id, request);
    }

    @DeleteMapping("/api/budgets/{id}")
    public ResponseEntity<Void> deleteBudget(Authentication authentication, @PathVariable Long id) {
        budgetService.deleteBudget(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
