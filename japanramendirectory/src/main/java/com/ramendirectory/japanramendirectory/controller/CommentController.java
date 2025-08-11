package com.ramendirectory.japanramendirectory.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ramendirectory.japanramendirectory.config.CommentRateLimitConfig;
import com.ramendirectory.japanramendirectory.dto.CommentDTO;
import com.ramendirectory.japanramendirectory.dto.CommentRequestDTO;
import com.ramendirectory.japanramendirectory.dto.ReportCommentDTO;
import com.ramendirectory.japanramendirectory.model.Comment;
import com.ramendirectory.japanramendirectory.model.Restaurant;
import com.ramendirectory.japanramendirectory.service.CommentService;
import com.ramendirectory.japanramendirectory.service.UserService;
import com.ramendirectory.japanramendirectory.util.IPAddressUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comments")
@Validated
public class CommentController {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    
    private final CommentService commentService;
    private final UserService userService;
    private final CommentRateLimitConfig rateLimitConfig;
    
    @Autowired
    public CommentController(
            CommentService commentService, 
            UserService userService,
            CommentRateLimitConfig rateLimitConfig) {
        this.commentService = commentService;
        this.userService = userService;
        this.rateLimitConfig = rateLimitConfig;
    }
    
    /**
     * Helper method to get username from the current authentication context
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        
        if (authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            return authentication.getName();
        }
    }
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createComment(
            @Valid @RequestBody CommentRequestDTO commentDTO, 
            HttpServletRequest request) {
        
        try {
            // Get client IP address for rate limiting
            String clientIP = IPAddressUtil.getClientIP(request);
            
            // Apply rate limiting - DISABLED FOR DEBUGGING
            // if (!rateLimitConfig.tryConsume(clientIP)) {
            //    logger.warn("Comment rate limit exceeded for IP: {}", maskIP(clientIP));
            //    return ResponseEntity
            //        .status(HttpStatus.TOO_MANY_REQUESTS)
            //        .body("Too many comment submissions. Please try again after " + 
            //             rateLimitConfig.getRefillMinutes() + " minutes.");
            // }
            
            // Log rate limit check (disabled)
            logger.info("Rate limiting disabled for debugging - createComment");
            
            // Get authentication manually instead of using @AuthenticationPrincipal
            String username = getCurrentUsername();
            Long userId = userService.findByUsername(username).getId();
            
            // Create the comment
            CommentDTO createdComment = commentService.createComment(commentDTO, userId);
            logger.info("New comment created for restaurant {} by user {}", 
                    commentDTO.getRestaurantId(), userId);
            
            return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Comment creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error in comment creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
    
    @PutMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequestDTO commentDTO,
            HttpServletRequest request) {
        
        try {
            // Get client IP address for rate limiting
            String clientIP = IPAddressUtil.getClientIP(request);
            
            // Apply rate limiting - DISABLED FOR DEBUGGING
            // if (!rateLimitConfig.tryConsume(clientIP)) {
            //    logger.warn("Comment update rate limit exceeded for IP: {}", maskIP(clientIP));
            //    return ResponseEntity
            //        .status(HttpStatus.TOO_MANY_REQUESTS)
            //        .body("Too many comment updates. Please try again after " + 
            //              rateLimitConfig.getRefillMinutes() + " minutes.");
            // }
            
            // Log rate limit check (disabled)
            logger.info("Rate limiting disabled for debugging - updateComment");
            
            // Get user ID from authenticated user
            String username = getCurrentUsername();
            Long userId = userService.findByUsername(username).getId();
            
            // Update the comment
            CommentDTO updatedComment = commentService.updateComment(commentId, commentDTO, userId);
            logger.info("Comment {} updated by user {}", commentId, userId);
            
            return new ResponseEntity<>(updatedComment, HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Comment update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating comment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the comment");
        }
    }
    
    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId) {
        
        try {
            // Get user ID from authenticated user
            String username = getCurrentUsername();
            Long userId = userService.findByUsername(username).getId();
            
            // Delete the comment
            commentService.deleteComment(commentId, userId);
            logger.info("Comment {} deleted by user {}", commentId, userId);
            
            return ResponseEntity.noContent().build();
            
        } catch (IllegalArgumentException e) {
            logger.warn("Comment deletion failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting comment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the comment");
        }
    }
    
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByRestaurant(@PathVariable Long restaurantId) {
        List<CommentDTO> comments = commentService.getCommentsByRestaurant(restaurantId);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/restaurant/{restaurantId}/paginated")
    public ResponseEntity<Page<CommentDTO>> getCommentsByRestaurantPaginated(
            @PathVariable Long restaurantId, 
            Pageable pageable) {
        Page<CommentDTO> comments = commentService.getCommentsByRestaurantPaginated(restaurantId, pageable);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CommentDTO>> getUserComments() {
        try {
            String username = getCurrentUsername();
            Long userId = userService.findByUsername(username).getId();
            List<CommentDTO> comments = commentService.getCommentsByUser(userId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            logger.error("Error getting user comments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    @GetMapping("/{commentId}")
    public ResponseEntity<?> getCommentById(@PathVariable Long commentId) {
        Optional<CommentDTO> comment = commentService.getCommentById(commentId);
        return comment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/report")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> reportComment(
            @Valid @RequestBody ReportCommentDTO reportDTO,
            HttpServletRequest request) {
        
        try {
            // Get client IP address for rate limiting
            String clientIP = IPAddressUtil.getClientIP(request);
            
            // Apply rate limiting - DISABLED FOR DEBUGGING
            // if (!rateLimitConfig.tryConsume(clientIP)) {
            //    logger.warn("Comment report rate limit exceeded for IP: {}", maskIP(clientIP));
            //    return ResponseEntity
            //        .status(HttpStatus.TOO_MANY_REQUESTS)
            //        .body("Too many report submissions. Please try again after " + 
            //              rateLimitConfig.getRefillMinutes() + " minutes.");
            // }
            
            // Log rate limit check (disabled)
            logger.info("Rate limiting disabled for debugging - reportComment");
            
            commentService.reportComment(reportDTO);
            logger.info("Comment {} reported", reportDTO.getCommentId());
            
            return ResponseEntity.ok().body("Comment reported successfully");
            
        } catch (IllegalArgumentException e) {
            logger.warn("Comment report failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error reporting comment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while reporting the comment");
        }
    }
    
    @GetMapping("/reported")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CommentDTO>> getReportedComments() {
        List<CommentDTO> reportedComments = commentService.getReportedComments();
        return ResponseEntity.ok(reportedComments);
    }
    
    @PutMapping("/reported/{commentId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reviewReportedComment(
            @PathVariable Long commentId,
            @RequestBody boolean approve) {
        
        try {
            CommentDTO reviewedComment = commentService.reviewReportedComment(commentId, approve);
            String action = approve ? "approved" : "rejected";
            logger.info("Reported comment {} has been {}", commentId, action);
            
            return ResponseEntity.ok(reviewedComment);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Comment review failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error reviewing comment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while reviewing the comment");
        }
    }
    
    @GetMapping("/has-commented/{restaurantId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> hasUserCommentedOnRestaurant(
            @PathVariable Long restaurantId) {
        
        try {
            String username = getCurrentUsername();
            Long userId = userService.findByUsername(username).getId();
            boolean hasCommented = commentService.hasUserCommentedOnRestaurant(userId, restaurantId);
            
            return ResponseEntity.ok(hasCommented);
        } catch (Exception e) {
            logger.error("Error checking if user has commented: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    @GetMapping("/can-comment/{restaurantId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> canUserCommentOnRestaurant(@PathVariable Long restaurantId) {
        try {
            String username = getCurrentUsername();
            Long userId = userService.findByUsername(username).getId();
            
            boolean canComment = commentService.canUserCommentOnRestaurant(userId, restaurantId);
            Map<String, Object> response = new HashMap<>();
            response.put("canComment", canComment);
            
            if (!canComment) {
                Optional<LocalDateTime> nextCommentTime = commentService.getTimeWhenUserCanCommentAgain(userId, restaurantId);
                if (nextCommentTime.isPresent()) {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime nextTime = nextCommentTime.get();
                    
                    long hoursRemaining = now.until(nextTime, ChronoUnit.HOURS);
                    long minutesRemaining = now.until(nextTime, ChronoUnit.MINUTES) % 60;
                    
                    response.put("nextCommentTime", nextTime);
                    response.put("hoursRemaining", hoursRemaining);
                    response.put("minutesRemaining", minutesRemaining);
                    response.put("message", String.format("You can comment again in %d hours and %d minutes", 
                                                         hoursRemaining, minutesRemaining));
                }
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking if user can comment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while checking if user can comment");
        }
    }
    
    @GetMapping("/restaurant/{restaurantId}/photos")
    public ResponseEntity<List<Map<String, String>>> getPhotosByRestaurant(@PathVariable Long restaurantId) {
        try {
            List<Map<String, String>> photos = commentService.getPhotosByRestaurant(restaurantId);
            return ResponseEntity.ok(photos);
        } catch (IllegalArgumentException e) {
            logger.error("Error getting photos by restaurant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            logger.error("Error getting photos by restaurant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    @GetMapping("/restaurant/{restaurantId}/latest-photo")
    public ResponseEntity<?> getLatestPhotoForRestaurant(@PathVariable Long restaurantId) {
        try {
            Optional<String> photoUrl = commentService.getLatestPhotoUrlForRestaurant(restaurantId);
            if (photoUrl.isPresent()) {
                Map<String, String> response = new HashMap<>();
                response.put("photoUrl", photoUrl.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (IllegalArgumentException e) {
            logger.error("Error getting latest photo for restaurant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            logger.error("Error getting latest photo for restaurant: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    // Helper method to mask IP addresses for privacy in logs
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