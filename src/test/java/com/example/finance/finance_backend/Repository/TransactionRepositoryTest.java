package com.example.finance.finance_backend.Repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Model.TransactionType;
import com.example.finance.finance_backend.Model.User;

@SpringBootTest
@Transactional
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findsOnlyTransactionsForUserWithinDateRange() {
        User firstUser = userRepository.save(user("first@example.com"));
        User secondUser = userRepository.save(user("second@example.com"));
        YearMonth month = YearMonth.now();

        transactionRepository.saveAll(List.of(
                transaction(firstUser, month.atDay(1)),
                transaction(firstUser, month.atEndOfMonth()),
                transaction(firstUser, month.atDay(1).minusDays(1)),
                transaction(secondUser, month.atDay(1))));
        transactionRepository.flush();

        List<Transaction> result = StreamSupport.stream(transactionRepository
                .findByUserIdAndTransactionDateBetween(
                        firstUser.getId(), month.atDay(1), month.atEndOfMonth())
                .spliterator(), false)
                .toList();

        assertEquals(2, result.size());
        assertEquals(List.of(month.atDay(1), month.atEndOfMonth()),
                result.stream().map(Transaction::getTransactionDate).sorted().toList());
    }

    private User user(String email) {
        String username = email.substring(0, email.indexOf('@'));
        return new User(username, "Test", "User", email, "test-password-hash");
    }

    private Transaction transaction(User user, LocalDate date) {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("12.00"));
        transaction.setTransactionDate(date);
        transaction.setMerchant("Test merchant");
        transaction.setTransactionType(TransactionType.EXPENSE);
        transaction.setUser(user);
        return transaction;
    }
}
