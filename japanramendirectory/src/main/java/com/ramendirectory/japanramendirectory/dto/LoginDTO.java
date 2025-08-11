package com.ramendirectory.japanramendirectory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginDTO {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotBlank(message = "reCAPTCHA verification is required")
    private String recaptchaResponse;
    
    // Default constructor
    public LoginDTO() {
    }
    
    // Constructor with parameters
    public LoginDTO(String username, String password, String recaptchaResponse) {
        this.username = username;
        this.password = password;
        this.recaptchaResponse = recaptchaResponse;
    }
    
    // Getters and setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRecaptchaResponse() {
        return recaptchaResponse;
    }
    
    public void setRecaptchaResponse(String recaptchaResponse) {
        this.recaptchaResponse = recaptchaResponse;
    }
} 