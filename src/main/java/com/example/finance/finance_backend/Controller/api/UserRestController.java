package com.example.finance.finance_backend.Controller.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Repository.UserRepository;
import com.example.finance.finance_backend.Request.PasswordRequest;
import com.example.finance.finance_backend.Request.ProfileRequest;

import jakarta.validation.Valid;
import tools.jackson.databind.JsonNode;

@RestController
public class UserRestController {

    // Same as the dashboardLayout column length in User
    private static final int MAX_DASHBOARD_LAYOUT_LENGTH = 10000;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserRestController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/api/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/api/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/api/user/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // Profile of the logged in user. The user comes from the JWT token
    // (authentication.getName() is the email), so nobody can see another user's profile.
    @GetMapping("/api/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // Updates first and last name of the logged in user (user comes from the JWT token)
    @PutMapping("/api/profile")
    public ResponseEntity<User> updateProfile(Authentication authentication,
            @Valid @RequestBody ProfileRequest request) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        return ResponseEntity.ok(userRepository.save(user));
    }

    // Changes the password of the logged in user. The current password must be correct.
    @PutMapping("/api/profile/password")
    public ResponseEntity<String> changePassword(Authentication authentication,
            @Valid @RequestBody PasswordRequest request) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            return ResponseEntity.badRequest().body("Current password is wrong");
        }
        // Saved encrypted, same as on register
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password changed");
    }

    // Dashboard layout of the logged in user, stored as JSON. 204 when the user has not
    // customized the dashboard yet, so the frontend shows its default layout.
    @GetMapping("/api/profile/dashboard")
    public ResponseEntity<String> getDashboardLayout(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (user.getDashboardLayout() == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(user.getDashboardLayout());
    }

    // Saves the dashboard layout of the logged in user. Any JSON object is accepted,
    // the frontend validates the widgets when it loads the layout.
    @PutMapping("/api/profile/dashboard")
    public ResponseEntity<String> updateDashboardLayout(Authentication authentication,
            @RequestBody JsonNode layout) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (!layout.isObject()) {
            return ResponseEntity.badRequest().body("Dashboard layout must be a JSON object");
        }
        String json = layout.toString();
        if (json.length() > MAX_DASHBOARD_LAYOUT_LENGTH) {
            return ResponseEntity.badRequest().body("Dashboard layout is too large");
        }
        user.setDashboardLayout(json);
        userRepository.save(user);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }

    // Deletes the logged in user's account, together with all their transactions and budgets
    @DeleteMapping("/api/profile")
    public ResponseEntity<Void> deleteAccount(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }
}