package com.example.finance.finance_backend.Controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.example.finance.finance_backend.Request.LoginRequest;
import com.example.finance.finance_backend.Request.RegisterRequest;
import com.example.finance.finance_backend.Service.JwtService;

import jakarta.validation.Valid;

import com.example.finance.finance_backend.Dto.AccessTokenPayloadDto;
import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Repository.UserRepository;

@Tag(name = "Authentication", description = "Register a new account, log in to get a JWT access token, and log out")
@RestController
public class AuthRestController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthRestController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    @Operation(summary = "Register a new user", description = "Creates a new user account. The username and email must both be unique. The password is stored as a BCrypt hash. Does not log the user in; call the login endpoint afterwards.")
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "Username or email is already in use")
    })
    @PostMapping("/api/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        //tarkista onko username ja email käytössä jo
        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
        Optional<User> existingEmail = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            /* System.out.println("Username is already in use!"); */
            return ResponseEntity.status(409).body("Username is already in use!");
        }
        if (existingEmail.isPresent()) {
            /* System.out.println("Email is already in use!"); */
            return ResponseEntity.status(409).body("Email is already in use!");
        }
        
        String passwordHash = passwordEncoder.encode(request.getPassword());
        // uusi käyttäjä luodaan
        User newUser = new User(
            request.getUsername(), 
            request.getFirstName(), 
            request.getLastName(), 
            request.getEmail(), 
            passwordHash
        );
        // käyttäjä tallennetaan tietokantaan
        userRepository.save(newUser);
        return ResponseEntity.status(201).body("User registered successfully!");
    }

    @Operation(summary = "Log in and get an access token", description = "Checks the email and password and returns a signed JWT access token with its expiry time. Send the token in the Authorization header as `Bearer <token>` on all other requests.")
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, returns the access token"),
            @ApiResponse(responseCode = "400", description = "Request body is invalid", content = @Content),
            @ApiResponse(responseCode = "403", description = "Invalid email or password", content = @Content)
    })
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest login, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        UsernamePasswordAuthenticationToken credentials =
            new UsernamePasswordAuthenticationToken(
                    login.getEmail(),
                    login.getPassword()
                );

        try {
            Authentication auth = authenticationManager.authenticate(credentials);

            AccessTokenPayloadDto token =
                jwtService.getAccessToken(auth.getName());

            return ResponseEntity.ok(token);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid email or password!");
        }

    }

    @Operation(summary = "Log out", description = "Clears the server side session and security context. JWT tokens are stateless, so the client should also delete its stored token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logged out", content = @Content),
            @ApiResponse(responseCode = "302", description = "Missing or invalid JWT token, redirects to /login", content = @Content)
    })
    @PostMapping("/api/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().build();
    }
}
