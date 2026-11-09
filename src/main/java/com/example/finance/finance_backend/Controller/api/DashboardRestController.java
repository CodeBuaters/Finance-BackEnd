package com.example.finance.finance_backend.Controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.finance.finance_backend.Dto.CategorySpendingDto;
import com.example.finance.finance_backend.Dto.MonthlySpendingDto;
import com.example.finance.finance_backend.Dto.SummaryDto;
import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Repository.UserRepository;
import com.example.finance.finance_backend.Service.CategorySpendingService;
import com.example.finance.finance_backend.Service.MonthlySpendingService;
import com.example.finance.finance_backend.Service.SummaryService;

@Tag(name = "Dashboard", description = "Aggregated statistics for the logged in user's dashboard charts")
@RestController
public class DashboardRestController {

    private final SummaryService summaryService;
    private final UserRepository userRepository;
    private final MonthlySpendingService monthlySpendingService;
    private final CategorySpendingService categorySpendingService;

    public DashboardRestController(SummaryService summaryService, UserRepository userRepository,
            MonthlySpendingService monthlySpendingService, CategorySpendingService categorySpendingService) {
        this.summaryService = summaryService;
        this.userRepository = userRepository;
        this.monthlySpendingService = monthlySpendingService;
        this.categorySpendingService = categorySpendingService;
    }

    @Operation(summary = "Get financial summary", description = "Returns the total income, total expenses, and balance (income minus expenses) of the logged in user. Savings is not implemented yet and is always 0.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Summary totals"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @GetMapping("/api/dashboard/summary")
    public SummaryDto getSummary(Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();

        Long userId = user.getId();

        return summaryService.getSummary(userId);
    }

    @Operation(summary = "Get monthly spending", description = "Returns total expenses grouped by month (YYYY-MM), for drawing a spending-over-time chart.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expenses per month"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @GetMapping("/api/dashboard/monthly")
    public List<MonthlySpendingDto> getMonthlySpending(Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();

        Long userId = user.getId();

        return monthlySpendingService.getMonthlySpending(userId);
    }

    @Operation(summary = "Get spending by category", description = "Returns total expenses grouped by category, for drawing a category breakdown chart.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expenses per category"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @GetMapping("/api/dashboard/categories")
    public List<CategorySpendingDto> getExpensesByCategory(Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();

        Long userId = user.getId();

        return categorySpendingService.getExpensesByCategory(userId);
    }

}
