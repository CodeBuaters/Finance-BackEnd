package com.example.finance.finance_backend.Controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@Tag(name = "Budgets", description = "Monthly spending limits per category for the logged in user")
@RestController
public class BudgetRestController {

    private final BudgetService budgetService;

    public BudgetRestController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // authentication.getName() is the logged in user's email

    @Operation(summary = "List my budgets", description = "Returns all budgets of the logged in user, across all months and categories.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of budgets"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @GetMapping("/api/budgets")
    public List<Budget> getBudgets(Authentication authentication) {
        return budgetService.getBudgets(authentication.getName());
    }

    @Operation(summary = "Get a budget", description = "Returns one budget by ID. Only budgets owned by the logged in user can be fetched.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The budget"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content),
            @ApiResponse(responseCode = "404", description = "Budget not found or owned by another user", content = @Content)
    })
    @GetMapping("/api/budgets/{id}")
    public Budget getBudget(Authentication authentication,
            @Parameter(description = "ID of the budget", example = "1") @PathVariable Long id) {
        return budgetService.getBudget(authentication.getName(), id);
    }

    @Operation(summary = "Create a budget", description = "Sets a spending limit for a category in a given month (format YYYY-MM). The amount must be greater than 0.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Budget created"),
            @ApiResponse(responseCode = "400", description = "Validation failed, e.g. missing category or wrong month format", content = @Content),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @PostMapping("/api/budgets")
    public ResponseEntity<Budget> createBudget(Authentication authentication,
            @Valid @RequestBody BudgetRequest request) {
        Budget budget = budgetService.createBudget(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(budget);
    }

    @Operation(summary = "Update a budget", description = "Replaces the category, amount and month of an existing budget owned by the logged in user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content),
            @ApiResponse(responseCode = "404", description = "Budget not found or owned by another user", content = @Content)
    })
    @PutMapping("/api/budgets/{id}")
    public Budget updateBudget(Authentication authentication,
            @Parameter(description = "ID of the budget to update", example = "1") @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {
        return budgetService.updateBudget(authentication.getName(), id, request);
    }

    @Operation(summary = "Delete a budget", description = "Permanently deletes a budget owned by the logged in user.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Budget deleted", content = @Content),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content),
            @ApiResponse(responseCode = "404", description = "Budget not found or owned by another user", content = @Content)
    })
    @DeleteMapping("/api/budgets/{id}")
    public ResponseEntity<Void> deleteBudget(Authentication authentication,
            @Parameter(description = "ID of the budget to delete", example = "1") @PathVariable Long id) {
        budgetService.deleteBudget(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
