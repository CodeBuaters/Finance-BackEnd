package com.example.finance.finance_backend.Controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@Tag(name = "Users & Profile", description = "The logged in user's profile, password, dashboard layout and account, plus user lookups")
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

    @Operation(summary = "List all users", description = "Returns every registered user. Password hashes are never included in the response.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @GetMapping("/api/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get a user by ID", description = "Returns one user by their ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The user"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/api/user/{id}")
    public ResponseEntity<User> getUserById(@Parameter(description = "ID of the user", example = "1") @PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get a user by email", description = "Returns one user by their email address.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The user"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/api/user/email/{email}")
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "Email address of the user", example = "anna@example.com") @PathVariable String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // Profile of the logged in user. The user comes from the JWT token
    // (authentication.getName() is the email), so nobody can see another user's profile.
    @Operation(summary = "Get my profile", description = "Returns the profile of the logged in user, taken from the JWT token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The user's profile"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content),
            @ApiResponse(responseCode = "404", description = "User in the token no longer exists", content = @Content)
    })
    @GetMapping("/api/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // Updates first and last name of the logged in user (user comes from the JWT token)
    @Operation(summary = "Update my name", description = "Changes the first and last name of the logged in user. Both are required.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "First or last name is blank", content = @Content),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content),
            @ApiResponse(responseCode = "404", description = "User in the token no longer exists", content = @Content)
    })
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
    @Operation(summary = "Change my password", description = "Changes the logged in user's password. The current password must be correct and the new one must be at least 8 characters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed"),
            @ApiResponse(responseCode = "400", description = "Current password is wrong, or the new password is too short"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content),
            @ApiResponse(responseCode = "404", description = "User in the token no longer exists", content = @Content)
    })
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
    @Operation(summary = "Get my dashboard layout", description = "Returns the logged in user's saved dashboard layout as JSON. Returns 204 if the user has not customized the dashboard yet, in which case the frontend shows its default layout.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The saved layout"),
            @ApiResponse(responseCode = "204", description = "No saved layout, use the default", content = @Content),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content),
            @ApiResponse(responseCode = "404", description = "User in the token no longer exists", content = @Content)
    })
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
    @Operation(summary = "Save my dashboard layout", description = "Saves the logged in user's dashboard layout. Any JSON object up to 10 000 characters is accepted; the frontend validates the widgets when loading it. Returns the saved layout.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Layout saved"),
            @ApiResponse(responseCode = "400", description = "Body is not a JSON object, or is too large"),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content),
            @ApiResponse(responseCode = "404", description = "User in the token no longer exists", content = @Content)
    })
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
    @Operation(summary = "Delete my account", description = "Permanently deletes the logged in user's account together with all their transactions and budgets. This cannot be undone.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account deleted", content = @Content),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content),
            @ApiResponse(responseCode = "404", description = "User in the token no longer exists", content = @Content)
    })
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