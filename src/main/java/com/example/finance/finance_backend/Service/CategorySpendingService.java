package com.example.finance.finance_backend.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.finance.finance_backend.Dto.CategorySpendingDto;
import com.example.finance.finance_backend.Model.Category;
import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Model.TransactionType;
import com.example.finance.finance_backend.Repository.CategoryRepository;
import com.example.finance.finance_backend.Repository.TransactionRepository;

@Service
public class CategorySpendingService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public CategorySpendingService(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    // Expenses of the current month grouped by category, e.g. FOOD 120.00, TRAVEL
    // 50.00.
    // Same month as SummaryService, so the totals add up to the summary's expenses.
    public List<CategorySpendingDto> getExpensesByCategory(Long userId) {

        YearMonth currentMonth = YearMonth.now();

        Iterable<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                userId,
                currentMonth.atDay(1),
                currentMonth.atEndOfMonth());

        // Group expenses by category
        Map<Category, BigDecimal> totals = new HashMap<>();

        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType() == TransactionType.EXPENSE) {

                // Transactions without a category are assigned to the default "Other"
                // category
                Category category = transaction.getCategory();
                if (category == null) {
                    category = categoryRepository.findByNameAndUserIsNull("Other").orElse(null);
                }

                if (category == null) {
                    continue;
                }

                BigDecimal total = totals.getOrDefault(category, BigDecimal.ZERO);
                totals.put(category, total.add(transaction.getAmount()));
            }
        }

        List<CategorySpendingDto> result = new ArrayList<>();

        for (Map.Entry<Category, BigDecimal> entry : totals.entrySet()) {
            result.add(new CategorySpendingDto(entry.getKey(), entry.getValue()));
        }

        return result;
    }
}
