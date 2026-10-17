package com.example.finance.finance_backend.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Model.TransactionType;
import com.example.finance.finance_backend.Repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private SummaryService summaryService;

    @Test
    void calculatesIncomeExpensesAndBalanceForCurrentMonth() {
        Long userId = 42L;
        YearMonth currentMonth = YearMonth.now();
        when(transactionRepository.findByUserIdAndTransactionDateBetween(
                userId, currentMonth.atDay(1), currentMonth.atEndOfMonth()))
                .thenReturn(List.of(
                        transaction("250.00", TransactionType.INCOME),
                        transaction("75.50", TransactionType.EXPENSE),
                        transaction("20.00", TransactionType.TRANSFER)));

        var summary = summaryService.getSummary(userId);

        assertEquals(new BigDecimal("250.00"), summary.getIncome());
        assertEquals(new BigDecimal("75.50"), summary.getExpenses());
        assertEquals(new BigDecimal("174.50"), summary.getBalance());
        assertEquals(BigDecimal.ZERO, summary.getSavings());
    }

    @Test
    void returnsZeroTotalsWhenUserHasNoTransactionsThisMonth() {
        Long userId = 42L;
        YearMonth currentMonth = YearMonth.now();
        when(transactionRepository.findByUserIdAndTransactionDateBetween(
                userId, currentMonth.atDay(1), currentMonth.atEndOfMonth()))
                .thenReturn(List.of());

        var summary = summaryService.getSummary(userId);

        assertEquals(BigDecimal.ZERO, summary.getIncome());
        assertEquals(BigDecimal.ZERO, summary.getExpenses());
        assertEquals(BigDecimal.ZERO, summary.getBalance());
        assertEquals(BigDecimal.ZERO, summary.getSavings());
    }

    private Transaction transaction(String amount, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(amount));
        transaction.setTransactionDate(LocalDate.now());
        transaction.setTransactionType(type);
        return transaction;
    }
}
