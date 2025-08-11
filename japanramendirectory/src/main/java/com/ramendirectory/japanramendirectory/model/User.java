package com.ramendirectory.japanramendirectory.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    
    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date lockedUntil;
    
    @Column(nullable = false)
    private Boolean accountNonLocked = true;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
    
    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    
    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }
    
    public Date getLockedUntil() {
        return lockedUntil;
    }
    
    public void setLockedUntil(Date lockedUntil) {
        this.lockedUntil = lockedUntil;
    }
    
    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }
    
    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }
    
    // Convenience methods
    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }
    
    public void makeAdmin() {
        this.role = Role.ADMIN;
    }
    
    public void revokeAdmin() {
        this.role = Role.USER;
    }
    
    /**
     * Increments the failed login attempts counter
     */
    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
    }
    
    /**
     * Resets the failed login attempts counter
     */
    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.accountNonLocked = true;
        this.lockedUntil = null;
    }
    
    /**
     * Locks the account until the specified date
     */
    public void lock(Date until) {
        this.accountNonLocked = false;
        this.lockedUntil = until;
    }
    
    /**
     * Checks if the account is currently locked
     */
    public boolean isLocked() {
        if (!accountNonLocked && lockedUntil != null) {
            // If current time is after lockout period, unlock the account
            if (new Date().after(lockedUntil)) {
                this.accountNonLocked = true;
                return false;
            }
            return true;
        }
        return !accountNonLocked;
    }
}