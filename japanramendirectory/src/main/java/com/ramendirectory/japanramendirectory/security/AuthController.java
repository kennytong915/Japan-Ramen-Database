package com.ramendirectory.japanramendirectory.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ramendirectory.japanramendirectory.dto.AuthResponseDTO;
import com.ramendirectory.japanramendirectory.dto.LoginDTO;
import com.ramendirectory.japanramendirectory.model.User;
import com.ramendirectory.japanramendirectory.service.ReCaptchaService;
import com.ramendirectory.japanramendirectory.service.UserService;

import jakarta.validation.Valid;

@RestController
public class AuthController {
	private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final ReCaptchaService reCaptchaService;
    private final UserService userService;

    @Autowired
    public AuthController(TokenService tokenService, 
                         AuthenticationManager authenticationManager,
                         ReCaptchaService reCaptchaService,
                         UserService userService) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.reCaptchaService = reCaptchaService;
        this.userService = userService;
    }

    @PostMapping("auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        LOG.debug("Login requested for user: '{}'", loginDTO.getUsername());
        
        // Validate reCAPTCHA
        if (!reCaptchaService.validateCaptcha(loginDTO.getRecaptchaResponse())) {
            return ResponseEntity.badRequest().body("reCAPTCHA validation failed");
        }
        
        // Check if account is locked
        if (userService.isAccountLocked(loginDTO.getUsername())) {
            return ResponseEntity.badRequest().body("Your account is temporarily locked due to too many failed login attempts");
        }
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDTO.getUsername(), 
                    loginDTO.getPassword()
                )
            );
            
            // Reset failed attempts on successful login
            User user = userService.findByUsername(loginDTO.getUsername());
            userService.resetFailedAttempts(user);
            
            String token = tokenService.generateToken(authentication);
            LOG.debug("Token granted: {}", token);
            
            // Create and return the response DTO
            AuthResponseDTO authResponse = new AuthResponseDTO(
                token,
                user.getId(),
                user.getUsername(),
                user.getRole(),
                tokenService.getTokenExpirationMs()
            );
            
            return ResponseEntity.ok(authResponse);
        } catch (BadCredentialsException e) {
            // Increment failed attempts
            if (userService.findByUsername(loginDTO.getUsername()) != null) {
                userService.incrementFailedAttempts(userService.findByUsername(loginDTO.getUsername()));
            }
            return ResponseEntity.badRequest().body("Invalid username or password");
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body("Authentication failed: " + e.getMessage());
        }
    }
    
//    @GetMapping("example")
//    public String example(Authentication auth) {
//    	System.out.println("name:  " +auth.getName());
//    	System.out.println("princ: " + auth.getPrincipal());
//    	System.out.println("creds: " + auth.getCredentials());
//    	System.out.println(auth.getAuthorities());
//    	return "Hello, " + auth.getName();
//    }
//    
//    @GetMapping("adminonly")
//    public String example() {
//    	return "You must be an admin";
//    }
    
}
