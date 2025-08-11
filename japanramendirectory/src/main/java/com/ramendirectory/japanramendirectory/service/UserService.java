package com.ramendirectory.japanramendirectory.service;

import java.util.List;
import java.util.Optional;

import com.ramendirectory.japanramendirectory.dto.RegistrationDTO;
import com.ramendirectory.japanramendirectory.dto.UserDTO;
import com.ramendirectory.japanramendirectory.model.Role;
import com.ramendirectory.japanramendirectory.model.User;

public interface UserService {
    User createUser(User user);
    UserDTO registerUser(RegistrationDTO registrationDTO);
    Optional<User> updateUser(Long id, User userDetails);
    boolean deleteUser(Long id);
    User findByUsername(String username);
    Optional<User> findById(Long id);
    List<User> findAll();
    Optional<User> setUserRole(Long id, Role role);
    
    // Account lockout methods
    void incrementFailedAttempts(User user);
    void resetFailedAttempts(User user);
    void lockUser(User user);
    boolean isAccountLocked(String username);
}
