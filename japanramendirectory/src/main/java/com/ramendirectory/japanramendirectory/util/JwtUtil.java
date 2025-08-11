package com.ramendirectory.japanramendirectory.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    /**
     * Extract the user ID from the JWT claims.
     * 
     * @param jwt The JWT token
     * @return The user ID as a Long, or null if not found or invalid
     */
    public Long getUserIdFromJwt(Jwt jwt) {
        if (jwt == null) {
            logger.warn("JWT is null");
            return null;
        }
        
        try {
            // The user ID is stored in the 'id' claim of the JWT
            Object idClaim = jwt.getClaim("id");
            
            if (idClaim == null) {
                logger.warn("No 'id' claim found in JWT");
                return null;
            }
            
            // Convert the ID to a Long
            if (idClaim instanceof Integer) {
                return ((Integer) idClaim).longValue();
            } else if (idClaim instanceof Long) {
                return (Long) idClaim;
            } else if (idClaim instanceof String) {
                return Long.parseLong((String) idClaim);
            } else {
                logger.warn("ID claim is of unexpected type: {}", idClaim.getClass().getName());
                return null;
            }
        } catch (Exception e) {
            logger.error("Error extracting user ID from JWT: {}", e.getMessage());
            return null;
        }
    }
} 