package com.example.finance.finance_backend.Controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deniesDashboardAccessWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void deniesDashboardAccessWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().is3xxRedirection());
    }
}
