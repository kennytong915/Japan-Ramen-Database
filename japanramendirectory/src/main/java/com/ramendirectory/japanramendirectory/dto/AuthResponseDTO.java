package com.ramendirectory.japanramendirectory.dto;

import com.ramendirectory.japanramendirectory.model.Role;

/**
 * DTO for authentication response
 */
public class AuthResponseDTO {
    private String token;
    private Long userId;
    private String username;
    private Role role;
    private long expiresIn;
    
    // Default constructor
    public AuthResponseDTO() {
    }
    
    // Constructor with parameters
    public AuthResponseDTO(String token, Long userId, String username, Role role, long expiresIn) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.expiresIn = expiresIn;
    }
    
    // Getters and setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
} 