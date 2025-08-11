package com.ramendirectory.japanramendirectory.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ramendirectory.japanramendirectory.dto.CommentDTO;
import com.ramendirectory.japanramendirectory.dto.CommentRequestDTO;
import com.ramendirectory.japanramendirectory.dto.ReportCommentDTO;
import com.ramendirectory.japanramendirectory.model.Comment;

public interface CommentService {
    
    // Create a new comment
    CommentDTO createComment(CommentRequestDTO commentDTO, Long userId);
    
    // Update an existing comment
    CommentDTO updateComment(Long commentId, CommentRequestDTO commentDTO, Long userId);
    
    // Delete a comment
    void deleteComment(Long commentId, Long userId);
    
    // Get all comments for a restaurant
    List<CommentDTO> getCommentsByRestaurant(Long restaurantId);
    
    // Get paginated comments for a restaurant
    Page<CommentDTO> getCommentsByRestaurantPaginated(Long restaurantId, Pageable pageable);
    
    // Get comments created by a specific user
    List<CommentDTO> getCommentsByUser(Long userId);
    
    // Get a specific comment by ID
    Optional<CommentDTO> getCommentById(Long commentId);
    
    // Check if user has already commented on restaurant
    boolean hasUserCommentedOnRestaurant(Long userId, Long restaurantId);
    
    // Check if user has commented on restaurant within the specified time period
    boolean hasUserCommentedOnRestaurantSince(Long userId, Long restaurantId, LocalDateTime since);
    
    // Check if user can comment on restaurant (at least one day since last comment)
    boolean canUserCommentOnRestaurant(Long userId, Long restaurantId);
    
    // Get time remaining until user can comment again
    Optional<LocalDateTime> getTimeWhenUserCanCommentAgain(Long userId, Long restaurantId);
    
    // Report a comment for inappropriate content
    void reportComment(ReportCommentDTO reportDTO);
    
    // Get all reported comments (admin function)
    List<CommentDTO> getReportedComments();
    
    // Approve or reject a reported comment (admin function)
    CommentDTO reviewReportedComment(Long commentId, boolean approve);
    
    // Process comments with content filter (internal use)
    Comment filterCommentContent(Comment comment);
    
    // Get all photos from a restaurant's comments
    List<Map<String, String>> getPhotosByRestaurant(Long restaurantId);
    
    // Get the latest photo URL from a restaurant's comments (for thumbnail)
    Optional<String> getLatestPhotoUrlForRestaurant(Long restaurantId);
} 