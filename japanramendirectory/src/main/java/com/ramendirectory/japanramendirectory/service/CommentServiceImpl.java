package com.ramendirectory.japanramendirectory.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ramendirectory.japanramendirectory.dto.CommentDTO;
import com.ramendirectory.japanramendirectory.dto.CommentRequestDTO;
import com.ramendirectory.japanramendirectory.dto.ReportCommentDTO;
import com.ramendirectory.japanramendirectory.model.Comment;
import com.ramendirectory.japanramendirectory.model.Restaurant;
import com.ramendirectory.japanramendirectory.model.Role;
import com.ramendirectory.japanramendirectory.model.User;
import com.ramendirectory.japanramendirectory.repository.CommentRepository;
import com.ramendirectory.japanramendirectory.repository.RestaurantRepository;
import com.ramendirectory.japanramendirectory.repository.UserRepository;

@Service
public class CommentServiceImpl implements CommentService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
    private static final int COMMENT_COOLDOWN_HOURS = 0; // Disabled for debugging (was 24 hours)
    
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ContentFilterService contentFilterService;
    
    @Autowired
    public CommentServiceImpl(
            CommentRepository commentRepository,
            UserRepository userRepository,
            RestaurantRepository restaurantRepository,
            ContentFilterService contentFilterService
    ) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.contentFilterService = contentFilterService;
    }
    
    @Override
    @Transactional
    public CommentDTO createComment(CommentRequestDTO commentDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Restaurant restaurant = restaurantRepository.findById(commentDTO.getRestaurantId())
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        
        // Check if user can comment on this restaurant
        if (!canUserCommentOnRestaurant(userId, commentDTO.getRestaurantId())) {
            Optional<LocalDateTime> nextCommentTime = getTimeWhenUserCanCommentAgain(userId, commentDTO.getRestaurantId());
            if (nextCommentTime.isPresent()) {
                LocalDateTime now = LocalDateTime.now();
                long hoursRemaining = now.until(nextCommentTime.get(), ChronoUnit.HOURS);
                long minutesRemaining = now.until(nextCommentTime.get(), ChronoUnit.MINUTES) % 60;
                
                throw new IllegalArgumentException(
                    String.format("You can comment on this restaurant again in %d hours and %d minutes", 
                                 hoursRemaining, minutesRemaining));
            } else {
                throw new IllegalArgumentException("You have already commented on this restaurant");
            }
        }
        
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setRestaurant(restaurant);
        comment.setFoodComment(commentDTO.getFoodComment());
        comment.setVisitingComment(commentDTO.getVisitingComment());
        comment.setEnvironmentComment(commentDTO.getEnvironmentComment());
        comment.setFoodScore(commentDTO.getFoodScore());
        comment.setVisitingScore(commentDTO.getVisitingScore());
        comment.setEnvironmentScore(commentDTO.getEnvironmentScore());
        comment.setOverallScore(commentDTO.getOverallScore());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setApproved(true);
        
        // Apply content filtering
        Comment filteredComment = filterCommentContent(comment);
        
        Comment savedComment = commentRepository.save(filteredComment);
        logger.info("New comment created for restaurant {} by user {}", restaurant.getId(), user.getId());
        
        return CommentDTO.fromEntity(savedComment);
    }
    
    @Override
    @Transactional
    public CommentDTO updateComment(Long commentId, CommentRequestDTO commentDTO, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        
        // Check if the user is the owner of the comment
        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can only update your own comments");
        }
        
        // Update comment fields
        comment.setFoodComment(commentDTO.getFoodComment());
        comment.setVisitingComment(commentDTO.getVisitingComment());
        comment.setEnvironmentComment(commentDTO.getEnvironmentComment());
        comment.setFoodScore(commentDTO.getFoodScore());
        comment.setVisitingScore(commentDTO.getVisitingScore());
        comment.setEnvironmentScore(commentDTO.getEnvironmentScore());
        comment.setOverallScore(commentDTO.getOverallScore());
        comment.setUpdatedAt(LocalDateTime.now());
        
        // Apply content filtering
        Comment filteredComment = filterCommentContent(comment);
        
        Comment savedComment = commentRepository.save(filteredComment);
        logger.info("Comment {} updated by user {}", commentId, userId);
        
        return CommentDTO.fromEntity(savedComment);
    }
    
    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Check if the user is the owner of the comment or an admin
        if (!comment.getUser().getId().equals(userId) && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can only delete your own comments");
        }
        
        commentRepository.delete(comment);
        logger.info("Comment {} deleted by {}", commentId, userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        
        return commentRepository.findByRestaurantAndApprovedTrueOrderByCreatedAtDesc(restaurant)
                .stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CommentDTO> getCommentsByRestaurantPaginated(Long restaurantId, Pageable pageable) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        
        return commentRepository.findByRestaurantAndApprovedTrue(restaurant, pageable)
                .map(CommentDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return commentRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDTO> getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .map(CommentDTO::fromEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasUserCommentedOnRestaurant(Long userId, Long restaurantId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        
        return commentRepository.existsByRestaurantAndUser(restaurant, user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasUserCommentedOnRestaurantSince(Long userId, Long restaurantId, LocalDateTime since) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        
        return commentRepository.existsByRestaurantAndUserAndCreatedAtAfter(restaurant, user, since);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean canUserCommentOnRestaurant(Long userId, Long restaurantId) {
        // Check if user has commented in the last 24 hours
        LocalDateTime oneDayAgo = LocalDateTime.now().minusHours(COMMENT_COOLDOWN_HOURS);
        return !hasUserCommentedOnRestaurantSince(userId, restaurantId, oneDayAgo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<LocalDateTime> getTimeWhenUserCanCommentAgain(Long userId, Long restaurantId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        
        // Get the most recent comment
        Pageable topOne = PageRequest.of(0, 1);
        List<Comment> latestComments = commentRepository.findLatestByUserAndRestaurant(user, restaurant, topOne);
        
        if (latestComments.isEmpty()) {
            return Optional.empty(); // No comment found, user can comment now
        }
        
        Comment latestComment = latestComments.get(0);
        LocalDateTime commentTime = latestComment.getCreatedAt();
        LocalDateTime canCommentAgainAt = commentTime.plusHours(COMMENT_COOLDOWN_HOURS);
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(canCommentAgainAt)) {
            return Optional.empty(); // Cooldown period has passed, user can comment now
        }
        
        return Optional.of(canCommentAgainAt);
    }
    
    @Override
    @Transactional
    public void reportComment(ReportCommentDTO reportDTO) {
        Comment comment = commentRepository.findById(reportDTO.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        
        comment.setReported(true);
        comment.setReportReason(reportDTO.getReason());
        comment.setReportedAt(LocalDateTime.now());
        
        commentRepository.save(comment);
        logger.info("Comment {} reported for: {}", comment.getId(), reportDTO.getReason());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getReportedComments() {
        return commentRepository.findByReportedTrue()
                .stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public CommentDTO reviewReportedComment(Long commentId, boolean approve) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        
        if (!comment.isReported()) {
            throw new IllegalArgumentException("Comment is not reported");
        }
        
        comment.setApproved(approve);
        if (approve) {
            comment.setReported(false);
            comment.setReportReason(null);
            logger.info("Reported comment {} has been approved", commentId);
        } else {
            logger.info("Reported comment {} has been rejected", commentId);
        }
        
        Comment savedComment = commentRepository.save(comment);
        return CommentDTO.fromEntity(savedComment);
    }
    
    @Override
    public Comment filterCommentContent(Comment comment) {
        // Apply content filtering to all text fields
        String foodComment = contentFilterService.filterText(comment.getFoodComment());
        String visitingComment = contentFilterService.filterText(comment.getVisitingComment());
        String environmentComment = contentFilterService.filterText(comment.getEnvironmentComment());
        
        // Check if any inappropriate content was filtered
        boolean wasFiltered = !foodComment.equals(comment.getFoodComment()) ||
                           !visitingComment.equals(comment.getVisitingComment()) ||
                           !environmentComment.equals(comment.getEnvironmentComment());
        
        if (wasFiltered) {
            logger.info("Inappropriate content filtered from comment for restaurant {}", 
                    comment.getRestaurant().getId());
        }
        
        // Update comment with filtered content
        comment.setFoodComment(foodComment);
        comment.setVisitingComment(visitingComment);
        comment.setEnvironmentComment(environmentComment);
        
        return comment;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, String>> getPhotosByRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        
        // Get all approved comments for the restaurant
        List<Comment> comments = commentRepository.findByRestaurantAndApprovedTrueOrderByCreatedAtDesc(restaurant);
        
        // Extract photos with usernames
        List<Map<String, String>> photos = new ArrayList<>();
        
        for (Comment comment : comments) {
            String username = comment.getUser().getUsername();
            
            if (comment.getPhotos() != null && !comment.getPhotos().isEmpty()) {
                for (String photoUrl : comment.getPhotos()) {
                    Map<String, String> photoData = new HashMap<>();
                    photoData.put("url", photoUrl);
                    photoData.put("username", username);
                    photos.add(photoData);
                }
            }
        }
        
        return photos;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<String> getLatestPhotoUrlForRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        
        // Get all approved comments for the restaurant, ordered by creation date (newest first)
        List<Comment> comments = commentRepository.findByRestaurantAndApprovedTrueOrderByCreatedAtDesc(restaurant);
        
        // Find the first comment with photos
        for (Comment comment : comments) {
            if (comment.getPhotos() != null && !comment.getPhotos().isEmpty()) {
                // Return the first photo URL from the most recent comment with photos
                return Optional.of(comment.getPhotos().get(0));
            }
        }
        
        // No photos found
        return Optional.empty();
    }
} 