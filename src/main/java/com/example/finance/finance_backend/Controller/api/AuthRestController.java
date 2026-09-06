package com.example.finance.finance_backend.Controller.api;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.finance.finance_backend.Request.LoginRequest;
import com.example.finance.finance_backend.Request.RegisterRequest;
import com.example.finance.finance_backend.Service.JwtService;

import jakarta.validation.Valid;

import com.example.finance.finance_backend.Dto.AccessTokenPayloadDto;
import com.example.finance.finance_backend.Model.User;
import com.example.finance.finance_backend.Repository.UserRepository;

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
}
