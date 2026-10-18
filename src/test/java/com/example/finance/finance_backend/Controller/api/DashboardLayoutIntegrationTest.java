package com.example.finance.finance_backend.Controller.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Repository.UserRepository;
import com.example.finance.finance_backend.Service.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DashboardLayoutIntegrationTest {

    private static final String LAYOUT =
            "{\"version\":1,\"widgets\":[{\"id\":\"kpis\",\"visible\":true,\"size\":\"full\"}]}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void returnsNoContentWhenTheDashboardHasNotBeenCustomized() throws Exception {
        User alice = createUser("alice");

        mockMvc.perform(get("/api/profile/dashboard")
                .header("Authorization", bearerToken(alice)))
                .andExpect(status().isNoContent());
    }

    @Test
    void savesAndReturnsTheLayout() throws Exception {
        User alice = createUser("alice");

        mockMvc.perform(put("/api/profile/dashboard")
                .header("Authorization", bearerToken(alice))
                .contentType(MediaType.APPLICATION_JSON)
                .content(LAYOUT))
                .andExpect(status().isOk())
                .andExpect(content().json(LAYOUT));

        mockMvc.perform(get("/api/profile/dashboard")
                .header("Authorization", bearerToken(alice)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(LAYOUT));
    }

    @Test
    void keepsEachUsersLayoutSeparate() throws Exception {
        User alice = createUser("alice");
        User bob = createUser("bob");

        mockMvc.perform(put("/api/profile/dashboard")
                .header("Authorization", bearerToken(alice))
                .contentType(MediaType.APPLICATION_JSON)
                .content(LAYOUT))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/profile/dashboard")
                .header("Authorization", bearerToken(bob)))
                .andExpect(status().isNoContent());
    }

    @Test
    void rejectsALayoutThatIsNotAJsonObject() throws Exception {
        User alice = createUser("alice");

        mockMvc.perform(put("/api/profile/dashboard")
                .header("Authorization", bearerToken(alice))
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1, 2, 3]"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/profile/dashboard")
                .header("Authorization", bearerToken(alice))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{not json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsATooLargeLayout() throws Exception {
        User alice = createUser("alice");
        String tooLarge = "{\"padding\":\"" + "x".repeat(10000) + "\"}";

        mockMvc.perform(put("/api/profile/dashboard")
                .header("Authorization", bearerToken(alice))
                .contentType(MediaType.APPLICATION_JSON)
                .content(tooLarge))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deniesAccessWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/profile/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    private User createUser(String username) {
        return userRepository.save(new User(
                username,
                username,
                "Test",
                username + "@example.com",
                passwordEncoder.encode("test-password")));
    }

    private String bearerToken(User user) {
        return "Bearer " + jwtService.getAccessToken(user.getEmail()).getAccessToken();
    }
}
