package com.ramendirectory.japanramendirectory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ramendirectory.japanramendirectory.config.RateLimitConfig;
import com.ramendirectory.japanramendirectory.dto.RegistrationDTO;
import com.ramendirectory.japanramendirectory.dto.UserDTO;
import com.ramendirectory.japanramendirectory.model.Role;
import com.ramendirectory.japanramendirectory.model.User;
import com.ramendirectory.japanramendirectory.service.ReCaptchaService;
import com.ramendirectory.japanramendirectory.service.UserService;
import com.ramendirectory.japanramendirectory.util.IPAddressUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final ReCaptchaService reCaptchaService;
    private final RateLimitConfig rateLimitConfig;

    @Autowired
    public UserController(UserService userService, ReCaptchaService reCaptchaService, RateLimitConfig rateLimitConfig) {
        this.userService = userService;
        this.reCaptchaService = reCaptchaService;
        this.rateLimitConfig = rateLimitConfig;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationDTO registrationDTO, HttpServletRequest request) {
        try {
            // Get client IP address
            String clientIP = IPAddressUtil.getClientIP(request);
            String maskedIP = maskIP(clientIP);
            
            logger.info("Registration attempt for username '{}' from IP: {}", 
                    registrationDTO.getUsername(), maskedIP);
            
            // Check rate limit
            if (!rateLimitConfig.tryConsume(clientIP)) {
                logger.warn("Registration rate limit exceeded for IP: {}", maskedIP);
                return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many registration attempts. Please try again after " + 
                          rateLimitConfig.getRefillMinutes() + " minutes.");
            }
            
            // Validate reCAPTCHA
            if (!reCaptchaService.validateCaptcha(registrationDTO.getRecaptchaResponse())) {
                logger.warn("reCAPTCHA validation failed for username '{}' from IP: {}", 
                        registrationDTO.getUsername(), maskedIP);
                return ResponseEntity.badRequest().body("reCAPTCHA validation failed");
            }
            
            UserDTO newUser = userService.registerUser(registrationDTO);
            logger.info("Registration successful for username '{}' from IP: {}", 
                    registrationDTO.getUsername(), maskedIP);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.warn("Registration failed with error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User newUser = userService.createUser(user);
            return new ResponseEntity<>(UserDTO.fromEntity(newUser), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDTO> userDTOs = users.stream()
            .map(UserDTO::fromEntity)
            .collect(Collectors.toList());
        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        if (user != null) {
            return ResponseEntity.ok(UserDTO.fromEntity(user));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userServiceImpl.findById(#id).isPresent() and authentication.principal.username == @userServiceImpl.findById(#id).get().getUsername()")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(value -> new ResponseEntity<>(UserDTO.fromEntity(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            return new ResponseEntity<>(UserDTO.fromEntity(user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userServiceImpl.findById(#id).isPresent() and authentication.principal.username == @userServiceImpl.findById(#id).get().getUsername()")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            Optional<User> updatedUser = userService.updateUser(id, user);
            return updatedUser.map(value -> new ResponseEntity<>(UserDTO.fromEntity(value), HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @PatchMapping("/{id}/role/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> promoteToAdmin(@PathVariable Long id) {
        Optional<User> updatedUser = userService.setUserRole(id, Role.ADMIN);
        return updatedUser.map(value -> new ResponseEntity<>(UserDTO.fromEntity(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PatchMapping("/{id}/role/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> demoteToUser(@PathVariable Long id) {
        Optional<User> updatedUser = userService.setUserRole(id, Role.USER);
        return updatedUser.map(value -> new ResponseEntity<>(UserDTO.fromEntity(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Masks part of an IP address for privacy in logs
     * 
     * @param ip the IP address to mask
     * @return the masked IP address
     */
    private String maskIP(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "unknown";
        }
        
        // Check if IPv6
        if (ip.contains(":")) {
            String[] parts = ip.split(":");
            if (parts.length > 2) {
                return parts[0] + ":" + parts[1] + ":x:x:x:x:x:x";
            }
            return ip; // Can't mask properly
        }
        
        // IPv4
        String[] parts = ip.split("\\.");
        if (parts.length == 4) {
            return parts[0] + "." + parts[1] + ".x.x";
        }
        
        return ip; // Can't mask properly
    }
} 