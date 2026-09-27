package com.example.finance.finance_backend.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.finance.finance_backend.Dto.MonthlySpendingDto;
import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Model.TransactionType;
import com.example.finance.finance_backend.Repository.TransactionRepository;

@Service
public class MonthlySpendingService {

    private final TransactionRepository transactionRepository;

    public MonthlySpendingService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<MonthlySpendingDto> getMonthlySpending(Long userId) {

        YearMonth currentMonth = YearMonth.now();
        YearMonth startMonth = currentMonth.minusMonths(11);

        List<MonthlySpendingDto> result = new ArrayList<>();

        Iterable<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                userId,
                startMonth.atDay(1),
                currentMonth.atEndOfMonth());

        for (YearMonth month = startMonth; !month.isAfter(currentMonth); month = month.plusMonths(1)) {

            BigDecimal total = BigDecimal.ZERO;

            for (Transaction transaction : transactions) {
                if (transaction.getTransactionType() == TransactionType.EXPENSE
                        && YearMonth.from(transaction.getTransactionDate()).equals(month)) {
                    total = total.add(transaction.getAmount());
                }
            }

            result.add(new MonthlySpendingDto(month.toString(), total));
        }

        return result;
    }
}
