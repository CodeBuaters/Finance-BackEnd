package com.example.finance.finance_backend.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.example.finance.finance_backend.Model.Category;
import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Model.TransactionType;
import com.example.finance.finance_backend.Repository.CategoryRepository;

class CsvImportServiceTest {

    private final RuleBasedCategorizationService categorizationService =
            mock(RuleBasedCategorizationService.class);
    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final CsvImportService csvImportService =
            new CsvImportService(categorizationService, categoryRepository);

    @Test
    void parsesValidCsvIntoTransactions() {
        Category food = category("Food");
        when(categoryRepository.findByNameAndUserIsNull("Food")).thenReturn(Optional.of(food));
        MockMultipartFile file = csvFile(
                "amount,merchant,date,type,category,description\n"
                        + "-12.50,Test Shop,2026-10-15,EXPENSE,Food,Weekly groceries\n");

        List<Transaction> transactions = toList(csvImportService.parseCsv(file));

        assertEquals(1, transactions.size());
        Transaction transaction = transactions.get(0);
        assertEquals(new BigDecimal("-12.50"), transaction.getAmount());
        assertEquals("Test Shop", transaction.getMerchant());
        assertEquals(LocalDate.of(2026, 10, 15), transaction.getTransactionDate());
        assertEquals(TransactionType.EXPENSE, transaction.getTransactionType());
        assertEquals(food, transaction.getCategory());
        assertEquals("Weekly groceries", transaction.getDescription());
    }

    @Test
    void categorizesByMerchantWhenCsvCategoryIsNotADefaultCategory() {
        Category other = category("Other");
        when(categoryRepository.findByNameAndUserIsNull("Unknown")).thenReturn(Optional.empty());
        when(categorizationService.categorize("Test Shop")).thenReturn(other);
        MockMultipartFile file = csvFile(
                "amount,merchant,category\n-12.50,Test Shop,Unknown\n");

        List<Transaction> transactions = toList(csvImportService.parseCsv(file));

        assertEquals(other, transactions.get(0).getCategory());
    }

    @Test
    void rejectsCsvWithInvalidAmount() {
        MockMultipartFile file = csvFile(
                "amount,merchant\nnot-a-number,Test Shop\n");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> csvImportService.parseCsv(file));

        assertEquals("Invalid amount: not-a-number", exception.getMessage());
    }

    private Category category(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }

    private MockMultipartFile csvFile(String content) {
        return new MockMultipartFile(
                "file",
                "transactions.csv",
                "text/csv",
                content.getBytes(StandardCharsets.UTF_8));
    }

    private List<Transaction> toList(Iterable<Transaction> transactions) {
        java.util.ArrayList<Transaction> result = new java.util.ArrayList<>();
        transactions.forEach(result::add);
        return result;
    }
}
