package com.example.finance.finance_backend.Request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login credentials")
public class LoginRequest {
    
    @Schema(description = "Email address of the account", example = "anna@example.com")
    private String email;
    @Schema(description = "Password of the account", example = "secret123")
    private String password;

    
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
