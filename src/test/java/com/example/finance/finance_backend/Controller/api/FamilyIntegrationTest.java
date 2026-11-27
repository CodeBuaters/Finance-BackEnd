package com.example.finance.finance_backend.Controller.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.finance.finance_backend.Model.Transaction;
import com.example.finance.finance_backend.Model.TransactionType;
import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Repository.FamilyRepository;
import com.example.finance.finance_backend.Repository.TransactionRepository;
import com.example.finance.finance_backend.Repository.UserRepository;
import com.example.finance.finance_backend.Service.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FamilyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private JwtService jwtService;

    @Test
    void familyDashboardAddsUpOnlyFamilyMembers() throws Exception {
        User alice = createUser("alice");
        User bob = createUser("bob");
        User eve = createUser("eve");
        createExpense(alice, "10.00");
        createExpense(bob, "5.00");
        createExpense(eve, "100.00");

        mockMvc.perform(post("/api/family").header("Authorization", bearerToken(alice))
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Smith family\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isString());
        String code = familyRepository.findAll().get(0).getCode();

        mockMvc.perform(post("/api/family/join").header("Authorization", bearerToken(bob))
                .contentType(MediaType.APPLICATION_JSON).content("{\"code\":\"" + code.toLowerCase() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members.length()").value(2))
                .andExpect(jsonPath("$.members[0].email").doesNotExist());

        // Family view adds up Alice and Bob, never Eve
        mockMvc.perform(get("/api/dashboard/summary?scope=family").header("Authorization", bearerToken(alice)))
                .andExpect(jsonPath("$.expenses").value(15.0));
        // Default view is still only the user
        mockMvc.perform(get("/api/dashboard/summary").header("Authorization", bearerToken(alice)))
                .andExpect(jsonPath("$.expenses").value(10.0));
        mockMvc.perform(get("/api/family/transactions").header("Authorization", bearerToken(bob)))
                .andExpect(jsonPath("$.length()").value(2));
        // Not in a family: the family view falls back to the user's own data
        mockMvc.perform(get("/api/dashboard/summary?scope=family").header("Authorization", bearerToken(eve)))
                .andExpect(jsonPath("$.expenses").value(100.0));
        mockMvc.perform(get("/api/family/transactions").header("Authorization", bearerToken(eve)))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void rejectsWrongCodeAndSecondFamily() throws Exception {
        User alice = createUser("alice");

        mockMvc.perform(post("/api/family/join").header("Authorization", bearerToken(alice))
                .contentType(MediaType.APPLICATION_JSON).content("{\"code\":\"WRONG123\"}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/family").header("Authorization", bearerToken(alice))
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"First\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/family").header("Authorization", bearerToken(alice))
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Second\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void lastMemberLeavingDeletesTheFamily() throws Exception {
        User alice = createUser("alice");

        mockMvc.perform(post("/api/family").header("Authorization", bearerToken(alice))
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Smith family\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/family/leave").header("Authorization", bearerToken(alice)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/family").header("Authorization", bearerToken(alice)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        assertEquals(0, familyRepository.count());
    }

    private User createUser(String username) {
        return userRepository.save(new User(username, username, "Test", username + "@example.com", "hash"));
    }

    private void createExpense(User user, String amount) {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(amount));
        transaction.setTransactionDate(LocalDate.now());
        transaction.setMerchant("Shop");
        transaction.setTransactionType(TransactionType.EXPENSE);
        transaction.setUser(user);
        transactionRepository.saveAndFlush(transaction);
    }

    private String bearerToken(User user) {
        return "Bearer " + jwtService.getAccessToken(user.getEmail()).getAccessToken();
    }
}
