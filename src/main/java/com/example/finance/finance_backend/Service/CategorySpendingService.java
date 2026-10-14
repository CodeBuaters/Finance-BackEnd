package com.example.finance.finance_backend.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.finance.finance_backend.Dto.CategorySpendingDto;
import com.example.finance.finance_backend.Model.Category;
import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Model.TransactionType;
import com.example.finance.finance_backend.Repository.TransactionRepository;

@Service
public class CategorySpendingService {

    private final TransactionRepository transactionRepository;

    public CategorySpendingService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Expenses of the current month grouped by category, e.g. FOOD 120.00, TRAVEL 50.00.
    // Same month as SummaryService, so the totals add up to the summary's expenses.
    public List<CategorySpendingDto> getExpensesByCategory(Long userId) {

        YearMonth currentMonth = YearMonth.now();

        Iterable<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                userId,
                currentMonth.atDay(1),
                currentMonth.atEndOfMonth());

        // EnumMap keeps the categories in the same order as in the Category enum
        Map<Category, BigDecimal> totals = new EnumMap<>(Category.class);

        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType() == TransactionType.EXPENSE) {

                // Transactions without a category are counted as OTHER
                Category category = transaction.getCategory();
                if (category == null) {
                    category = Category.OTHER;
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
