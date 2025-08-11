package com.ramendirectory.japanramendirectory.config;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@Component
public class CommentRateLimitConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentRateLimitConfig.class);
    
    @Value("${comment.rate.limit.capacity:10}")
    private long capacity;
    
    @Value("${comment.rate.limit.refill:5}")
    private long refillMinutes;
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    /**
     * Create a token bucket for rate limiting.
     * Default configuration allows 10 comments per IP every 5 minutes.
     * 
     * @return a configured token bucket
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.greedy(capacity, Duration.ofMinutes(refillMinutes)));
        return Bucket.builder().addLimit(limit).build();
    }
    
    /**
     * Get a bucket for an IP address, creating it if it doesn't exist
     * 
     * @param ipAddress the IP address to get a bucket for
     * @return the bucket for the given IP
     */
    public Bucket resolveBucket(String ipAddress) {
        Bucket bucket = buckets.computeIfAbsent(ipAddress, ip -> {
            logger.info("Creating new comment rate limit bucket for IP: {}", maskIP(ip));
            return createNewBucket();
        });
        return bucket;
    }
    
    /**
     * Check if a request should be allowed based on the rate limit
     * 
     * @param ipAddress the IP address making the request
     * @return true if the request should be allowed, false otherwise
     */
    public boolean tryConsume(String ipAddress) {
        Bucket bucket = resolveBucket(ipAddress);
        boolean allowed = bucket.tryConsume(1);
        long remaining = bucket.getAvailableTokens();
        
        if (allowed) {
            logger.info("Comment rate limit request allowed for IP: {}. Remaining attempts: {}/{}", 
                      maskIP(ipAddress), remaining, capacity);
        } else {
            logger.warn("Comment rate limit exceeded for IP: {}. Request rejected. Refill in {} minutes.", 
                      maskIP(ipAddress), refillMinutes);
        }
        
        return allowed;
    }
    
    /**
     * Get the remaining tokens for an IP address
     * 
     * @param ipAddress the IP address to check
     * @return the number of tokens remaining
     */
    public long getRemainingTokens(String ipAddress) {
        return resolveBucket(ipAddress).getAvailableTokens();
    }
    
    /**
     * Get the configured capacity of buckets
     * 
     * @return the capacity
     */
    public long getCapacity() {
        return capacity;
    }
    
    /**
     * Get the refill period in minutes
     * 
     * @return the refill period
     */
    public long getRefillMinutes() {
        return refillMinutes;
    }
    
    /**
     * Masks part of an IP address for privacy in logs
     * For IPv4: 192.168.1.1 becomes 192.168.x.x
     * For IPv6: 2001:0db8:85a3:0000:0000:8a2e:0370:7334 becomes 2001:0db8:x:x:x:x:x:x
     * 
     * @param ip the IP address to mask
     * @return the masked IP address
     */
    private String maskIP(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "unknown";
        }
        
        // For IPv4
        if (ip.contains(".")) {
            String[] parts = ip.split("\\.");
            if (parts.length == 4) {
                return parts[0] + "." + parts[1] + ".x.x";
            }
        }
        
        // For IPv6
        if (ip.contains(":")) {
            String[] parts = ip.split(":");
            if (parts.length > 2) {
                return parts[0] + ":" + parts[1] + ":x:x:x:x:x:x";
            }
        }
        
        return ip.substring(0, Math.min(ip.length(), 3)) + "...";
    }
} 