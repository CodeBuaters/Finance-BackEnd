package com.example.finance.finance_backend.Request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Details for a new user account")
public class RegisterRequest {
    
    @Schema(description = "Unique username", example = "anna")
    private String username;
    @Schema(description = "First name", example = "Anna")
    private String firstName;
    @Schema(description = "Last name", example = "Smith")
    private String lastName;
    @Schema(description = "Unique email address, used to log in", example = "anna@example.com")
    private String email;
    @Schema(description = "Password in plain text; stored hashed", example = "secret123")
    private String password;


    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
