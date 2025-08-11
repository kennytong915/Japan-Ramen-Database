package com.ramendirectory.japanramendirectory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationDTO {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]+$", 
        message = "Username can only contain letters, numbers, periods, underscores, and hyphens"
    )
    private String username;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$",
        message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character"
    )
    private String password;
    
    @NotBlank(message = "reCAPTCHA verification is required")
    private String recaptchaResponse;
    
    // Default constructor
    public RegistrationDTO() {
    }
    
    // Constructor with parameters
    public RegistrationDTO(String username, String password, String recaptchaResponse) {
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