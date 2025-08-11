package com.ramendirectory.japanramendirectory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ramendirectory.japanramendirectory.dto.RegistrationDTO;
import com.ramendirectory.japanramendirectory.dto.UserDTO;
import com.ramendirectory.japanramendirectory.model.Role;
import com.ramendirectory.japanramendirectory.model.User;
import com.ramendirectory.japanramendirectory.repository.UserRepository;
import com.ramendirectory.japanramendirectory.service.ContentFilterService;

import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.Calendar;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ContentFilterService contentFilterService;
    
    @Value("${security.max-failed-attempts:5}")
    private int maxFailedAttempts;
    
    @Value("${security.lock-time-duration:15}")
    private int lockTimeDuration;
    
    // Pattern for valid usernames
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,30}$");

    @Autowired
    public UserServiceImpl(UserRepository userRepository, 
                          PasswordEncoder passwordEncoder,
                          ContentFilterService contentFilterService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.contentFilterService = contentFilterService;
    }

    @Override
    public User createUser(User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        // Validate username
        validateUsername(user.getUsername());
        
        // Always set new users to USER role regardless of what's in the request
        user.setRole(Role.USER);
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    @Override
    public UserDTO registerUser(RegistrationDTO registrationDTO) {
        // Additional validation beyond the annotation constraints
        validateUsername(registrationDTO.getUsername());
        validatePassword(registrationDTO.getPassword());
        
        // Check if username already exists
        if (findByUsername(registrationDTO.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setRole(Role.USER);
        user.setAccountNonLocked(true);
        user.setFailedLoginAttempts(0);
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Return DTO
        return UserDTO.fromEntity(savedUser);
    }

    @Override
    public Optional<User> updateUser(Long id, User userDetails) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Validate new username if it's changing
            if (!user.getUsername().equals(userDetails.getUsername())) {
                validateUsername(userDetails.getUsername());
            }
            
            user.setUsername(userDetails.getUsername());

            // Only update password if provided
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                validatePassword(userDetails.getPassword());
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            
            // Set role if provided, otherwise preserve existing role
            if (userDetails.getRole() != null) {
                user.setRole(userDetails.getRole());
            }
            
            return Optional.of(userRepository.save(user));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    @Override
    public Optional<User> setUserRole(Long id, Role role) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRole(role);
            return Optional.of(userRepository.save(user));
        }
        return Optional.empty();
    }
    
    @Override
    public void incrementFailedAttempts(User user) {
        user.incrementFailedAttempts();
        
        // Check if max failed attempts reached
        if (user.getFailedLoginAttempts() >= maxFailedAttempts) {
            lockUser(user);
        }
        
        userRepository.save(user);
    }
    
    @Override
    public void resetFailedAttempts(User user) {
        user.resetFailedAttempts();
        userRepository.save(user);
    }
    
    @Override
    public void lockUser(User user) {
        // Calculate lock duration
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, lockTimeDuration);
        Date lockUntil = calendar.getTime();
        
        user.lock(lockUntil);
        userRepository.save(user);
    }
    
    @Override
    public boolean isAccountLocked(String username) {
        User user = findByUsername(username);
        if (user != null) {
            return user.isLocked();
        }
        return false;
    }
    
    /**
     * Validates a username against inappropriate content and pattern requirements
     * 
     * @param username the username to validate
     * @throws IllegalArgumentException if the username is invalid
     */
    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        // Check for inappropriate content
        if (contentFilterService.containsInappropriateContent(username)) {
            List<String> inappropriateWords = contentFilterService.getInappropriateWordsInText(username);
            logger.warn("Username '{}' rejected due to inappropriate content: {}", 
                    username, String.join(", ", inappropriateWords));
            throw new IllegalArgumentException("Username contains inappropriate content");
        }
        
        // Check pattern
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException(
                "Username must be 3-30 characters long and contain only letters, numbers, periods, underscores, and hyphens"
            );
        }
    }
    
    /**
     * Additional password validation beyond the constraints in RegistrationDTO
     * 
     * @param password the password to validate
     * @throws IllegalArgumentException if the password is invalid
     */
    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        // Check for minimum length
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        
        // Check for at least one digit
        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
        
        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        
        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        
        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }

    /**
     * Creates an admin user only if no users exist in the system
     * This should be called during application initialization
     * @param adminUsername Admin's username
     * @param adminPassword Admin's raw password (will be encoded)
     * @return The created admin user, or null if users already exist
     */
    public User createInitialAdminUser(String adminUsername, String adminPassword) {
        // Only create admin if no users exist
        if (userRepository.count() == 0) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRole(Role.ADMIN);
            return userRepository.save(adminUser);
        }
        return null;
    }
}