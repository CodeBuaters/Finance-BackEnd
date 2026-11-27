package com.example.finance.finance_backend.Controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.time.YearMonth;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.finance.finance_backend.Dto.CategorySpendingDto;
import com.example.finance.finance_backend.Dto.MonthlySpendingDto;
import com.example.finance.finance_backend.Dto.SummaryDto;
import com.example.finance.finance_backend.Service.CategorySpendingService;
import com.example.finance.finance_backend.Service.FamilyService;
import com.example.finance.finance_backend.Service.MonthlySpendingService;
import com.example.finance.finance_backend.Service.SummaryService;

@Tag(name = "Dashboard", description = "Aggregated statistics for the logged in user's dashboard charts")
@RestController
public class DashboardRestController {

    private final SummaryService summaryService;
    private final FamilyService familyService;
    private final MonthlySpendingService monthlySpendingService;
    private final CategorySpendingService categorySpendingService;

    public DashboardRestController(SummaryService summaryService, FamilyService familyService,
            MonthlySpendingService monthlySpendingService, CategorySpendingService categorySpendingService) {
        this.summaryService = summaryService;
        this.familyService = familyService;
        this.monthlySpendingService = monthlySpendingService;
        this.categorySpendingService = categorySpendingService;
    }

    @Operation(summary = "Get financial summary", description = "Returns the total income, total expenses, and balance (income minus expenses) of the logged in user. Savings is not implemented yet and is always 0.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Summary totals"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @GetMapping("/api/dashboard/summary")
    public SummaryDto getSummary(Authentication authentication,
            @Parameter(description = "\"family\" for the whole family, otherwise only you") @RequestParam(defaultValue = "me") String scope) {
        return summaryService.getSummary(familyService.getUserIds(authentication.getName(), scope));
    }

    @Operation(summary = "Get monthly spending", description = "Returns total expenses grouped by month (YYYY-MM), for drawing a spending-over-time chart.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expenses per month"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @GetMapping("/api/dashboard/monthly")
    public List<MonthlySpendingDto> getMonthlySpending(Authentication authentication,
            @Parameter(description = "\"family\" for the whole family, otherwise only you") @RequestParam(defaultValue = "me") String scope) {
        return monthlySpendingService.getMonthlySpending(familyService.getUserIds(authentication.getName(), scope));
    }

    @Operation(summary = "Get spending by category", description = "Returns total expenses grouped by category, for drawing a category breakdown chart.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expenses per category"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })

    @GetMapping("/api/dashboard/categories")
    public List<CategorySpendingDto> getExpensesByCategory(
            Authentication authentication,
            @Parameter(description = "\"family\" for the whole family, otherwise only you") @RequestParam(defaultValue = "me") String scope,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {

        List<Long> userIds = familyService.getUserIds(authentication.getName(), scope);

        if (month == null) {
            return categorySpendingService.getExpensesByCategory(userIds);
        }

        return categorySpendingService.getExpensesByCategory(userIds, month);
    }

}
