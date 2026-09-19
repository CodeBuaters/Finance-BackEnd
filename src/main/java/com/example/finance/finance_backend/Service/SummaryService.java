package com.example.finance.finance_backend.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.stereotype.Service;

import com.example.finance.finance_backend.Dto.SummaryDto;
import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Repository.TransactionRepository;
import com.example.finance.finance_backend.Model.TransactionType;

@Service
public class SummaryService {

    private final TransactionRepository transactionRepository;

    public SummaryService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public SummaryDto getSummary(Long userId) {
        YearMonth currentMonth = YearMonth.now();

        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        Iterable<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                userId,
                startDate,
                endDate);

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {

            if (transaction.getTransactionType() == TransactionType.INCOME) {
                income = income.add(transaction.getAmount());
            }

            if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                expenses = expenses.add(transaction.getAmount());
            }
        }

        BigDecimal balance = income.subtract(expenses);

        // savings puuttuu vielä! - esim. savingsgoal toiminto lisätään myöhemmin
        BigDecimal savings = BigDecimal.ZERO;

        return new SummaryDto(income, expenses, balance, savings);
    }
}
