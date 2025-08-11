package com.ramendirectory.japanramendirectory.controller;

import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramendirectory.japanramendirectory.dto.CommentDTO;
import com.ramendirectory.japanramendirectory.dto.CommentRequestDTO;
import com.ramendirectory.japanramendirectory.service.CommentService;
import com.ramendirectory.japanramendirectory.service.S3Service;
import com.ramendirectory.japanramendirectory.service.UserService;
import com.ramendirectory.japanramendirectory.util.IPAddressUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comments-multipart")
@Validated
public class CommentMultipartController {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentMultipartController.class);
    
    private final CommentService commentService;
    private final UserService userService;
    private final S3Service s3Service;
    private final ObjectMapper objectMapper;
    
    @Value("${app.comment.max-photos}")
    private int maxPhotosPerComment;
    
    @Autowired
    public CommentMultipartController(
            CommentService commentService, 
            UserService userService,
            S3Service s3Service,
            ObjectMapper objectMapper) {
        this.commentService = commentService;
        this.userService = userService;
        this.s3Service = s3Service;
        this.objectMapper = objectMapper;
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
    
    /**
     * Create a comment with photo uploads in a single request
     * 
     * @param commentDataStr JSON string containing the comment data
     * @param files Photo files to upload (optional)
     * @param request HTTP request for IP tracking
     * @return The created comment with photo URLs
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createCommentWithPhotos(
            @RequestPart("commentData") String commentDataStr,
            @RequestPart(name = "photos", required = false) MultipartFile[] files,
            HttpServletRequest request) {
        
        try {
            // Parse the comment JSON data
            CommentRequestDTO commentDTO = objectMapper.readValue(commentDataStr, CommentRequestDTO.class);
            
            // Get user ID
            String username = getCurrentUsername();
            Long userId = userService.findByUsername(username).getId();
            
            // Create the comment first
            CommentDTO createdComment = commentService.createComment(commentDTO, userId);
            logger.info("New comment created for restaurant {} by user {}", 
                    commentDTO.getRestaurantId(), userId);
            
            // Handle photo uploads if any
            List<String> uploadedPhotoUrls = new ArrayList<>();
            Map<String, String> errors = new HashMap<>();
            
            if (files != null && files.length > 0) {
                // Check if the number of photos doesn't exceed the limit
                if (files.length > maxPhotosPerComment) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Maximum " + maxPhotosPerComment + " photos allowed per comment");
                }
                
                // Upload each photo and add its URL to the comment
                for (int i = 0; i < files.length; i++) {
                    MultipartFile file = files[i];
                    
                    if (file.isEmpty()) {
                        continue;
                    }
                    
                    try {
                        String photoUrl = s3Service.uploadFile(file, userId, createdComment.getId());
                        uploadedPhotoUrls.add(photoUrl);
                        
                        // Since we don't have direct access to the comment entity here,
                        // we'll refresh the comment data after all uploads
                        
                    } catch (IOException e) {
                        logger.error("Error uploading file: {}", e.getMessage());
                        errors.put("file_" + i, "Error uploading: " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        logger.error("Invalid file: {}", e.getMessage());
                        errors.put("file_" + i, e.getMessage());
                    }
                }
                
                // Reload the comment to get the updated photo URLs
                // Note: In a real implementation, you might want to update the comment entity directly
                Optional<CommentDTO> updatedComment = commentService.getCommentById(createdComment.getId());
                if (updatedComment.isPresent()) {
                    createdComment = updatedComment.get();
                }
            }
            
            // Create response with comment and any upload errors
            Map<String, Object> response = new HashMap<>();
            response.put("comment", createdComment);
            
            if (!errors.isEmpty()) {
                response.put("photoErrors", errors);
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(response);
            }
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Comment creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error in comment creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
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