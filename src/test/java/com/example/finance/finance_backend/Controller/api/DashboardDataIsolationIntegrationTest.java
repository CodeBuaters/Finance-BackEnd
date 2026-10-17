package com.example.finance.finance_backend.Controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Model.TransactionType;
import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Repository.TransactionRepository;
import com.example.finance.finance_backend.Repository.UserRepository;
import com.example.finance.finance_backend.Service.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DashboardDataIsolationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void dashboardSummaryContainsOnlyTheAuthenticatedUsersTransactions() throws Exception {
        User alice = createUser("alice");
        User bob = createUser("bob");
        createTransaction(alice);
        createTransaction(bob);

        mockMvc.perform(get("/api/dashboard/summary")
                .header("Authorization", bearerToken(alice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expenses").value(10.0));
    }

    private User createUser(String username) {
        return userRepository.save(new User(
                username,
                username,
                "Test",
                username + "@example.com",
                passwordEncoder.encode("test-password")));
    }

    private Transaction createTransaction(User user) {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("10.00"));
        transaction.setTransactionDate(LocalDate.now());
        transaction.setMerchant(user.getUsername() + " merchant");
        transaction.setTransactionType(TransactionType.EXPENSE);
        transaction.setUser(user);
        return transactionRepository.saveAndFlush(transaction);
    }

    private String bearerToken(User user) {
        return "Bearer " + jwtService.getAccessToken(user.getEmail()).getAccessToken();
    }
}
