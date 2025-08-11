package com.ramendirectory.japanramendirectory.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ramendirectory.japanramendirectory.model.Comment;
import com.ramendirectory.japanramendirectory.model.Restaurant;
import com.ramendirectory.japanramendirectory.model.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Find all comments for a specific restaurant
    List<Comment> findByRestaurantOrderByCreatedAtDesc(Restaurant restaurant);
    
    // Find comments with pagination
    Page<Comment> findByRestaurant(Restaurant restaurant, Pageable pageable);
    
    // Find all comments by a specific user
    List<Comment> findByUserOrderByCreatedAtDesc(User user);
    
    // Find comments by restaurant and user
    List<Comment> findByRestaurantAndUser(Restaurant restaurant, User user);
    
    // Find reported comments
    List<Comment> findByReportedTrue();
    
    // Find all approved comments for a restaurant
    List<Comment> findByRestaurantAndApprovedTrueOrderByCreatedAtDesc(Restaurant restaurant);
    
    // Find all approved comments for a restaurant with pagination
    Page<Comment> findByRestaurantAndApprovedTrue(Restaurant restaurant, Pageable pageable);
    
    // Check if a user has already commented on a restaurant
    boolean existsByRestaurantAndUser(Restaurant restaurant, User user);
    
    // Check if a user has commented on a restaurant after a specific time
    boolean existsByRestaurantAndUserAndCreatedAtAfter(Restaurant restaurant, User user, LocalDateTime time);
    
    // Count comments by restaurant
    long countByRestaurant(Restaurant restaurant);
    
    // Count reported comments
    long countByReportedTrue();
    
    // Find the latest comment by user and restaurant
    @Query("SELECT c FROM Comment c WHERE c.user = :user AND c.restaurant = :restaurant ORDER BY c.createdAt DESC")
    List<Comment> findLatestByUserAndRestaurant(@Param("user") User user, @Param("restaurant") Restaurant restaurant, Pageable pageable);
} 