package com.example.finance.finance_backend.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.example.finance.finance_backend.Model.Category;
import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Model.TransactionType;

class CsvImportServiceTest {

    private final CsvImportService csvImportService =
            new CsvImportService(mock(RuleBasedCategorizationService.class));

    @Test
    void parsesValidCsvIntoTransactions() {
        MockMultipartFile file = csvFile(
                "amount,merchant,date,type,category,description\n"
                        + "-12.50,Test Shop,2026-10-15,EXPENSE,FOOD,Weekly groceries\n");

        List<Transaction> transactions = toList(csvImportService.parseCsv(file));

        assertEquals(1, transactions.size());
        Transaction transaction = transactions.get(0);
        assertEquals(new BigDecimal("-12.50"), transaction.getAmount());
        assertEquals("Test Shop", transaction.getMerchant());
        assertEquals(LocalDate.of(2026, 10, 15), transaction.getTransactionDate());
        assertEquals(TransactionType.EXPENSE, transaction.getTransactionType());
        assertEquals(Category.FOOD, transaction.getCategory());
        assertEquals("Weekly groceries", transaction.getDescription());
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
