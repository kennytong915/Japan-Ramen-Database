package com.ramendirectory.japanramendirectory.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ramendirectory.japanramendirectory.model.Comment;

public class CommentDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long restaurantId;
    private String restaurantName;
    private String foodComment;
    private String visitingComment;
    private String environmentComment;
    private Integer foodScore;
    private Integer visitingScore;
    private Integer environmentScore;
    private Integer overallScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private float averageScore;
    private List<String> photos;
    
    // Default constructor
    public CommentDTO() {
    }
    
    // Static method to convert Comment entity to DTO
    public static CommentDTO fromEntity(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.id = comment.getId();
        dto.userId = comment.getUser().getId();
        dto.username = comment.getUser().getUsername();
        dto.restaurantId = comment.getRestaurant().getId();
        dto.restaurantName = comment.getRestaurant().getName();
        dto.foodComment = comment.getFoodComment();
        dto.visitingComment = comment.getVisitingComment();
        dto.environmentComment = comment.getEnvironmentComment();
        dto.foodScore = comment.getFoodScore();
        dto.visitingScore = comment.getVisitingScore();
        dto.environmentScore = comment.getEnvironmentScore();
        dto.overallScore = comment.getOverallScore();
        dto.createdAt = comment.getCreatedAt();
        dto.updatedAt = comment.getUpdatedAt();
        dto.averageScore = comment.getAverageScore();
        
        // Copy photos if they exist
        if (comment.getPhotos() != null && !comment.getPhotos().isEmpty()) {
            dto.photos = new ArrayList<>(comment.getPhotos());
        } else {
            dto.photos = new ArrayList<>();
        }
        
        return dto;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Long getRestaurantId() {
        return restaurantId;
    }
    
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
    
    public String getRestaurantName() {
        return restaurantName;
    }
    
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
    
    public String getFoodComment() {
        return foodComment;
    }
    
    public void setFoodComment(String foodComment) {
        this.foodComment = foodComment;
    }
    
    public String getVisitingComment() {
        return visitingComment;
    }
    
    public void setVisitingComment(String visitingComment) {
        this.visitingComment = visitingComment;
    }
    
    public String getEnvironmentComment() {
        return environmentComment;
    }
    
    public void setEnvironmentComment(String environmentComment) {
        this.environmentComment = environmentComment;
    }
    
    public Integer getFoodScore() {
        return foodScore;
    }
    
    public void setFoodScore(Integer foodScore) {
        this.foodScore = foodScore;
    }
    
    public Integer getVisitingScore() {
        return visitingScore;
    }
    
    public void setVisitingScore(Integer visitingScore) {
        this.visitingScore = visitingScore;
    }
    
    public Integer getEnvironmentScore() {
        return environmentScore;
    }
    
    public void setEnvironmentScore(Integer environmentScore) {
        this.environmentScore = environmentScore;
    }
    
    public Integer getOverallScore() {
        return overallScore;
    }
    
    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public float getAverageScore() {
        return averageScore;
    }
    
    public void setAverageScore(float averageScore) {
        this.averageScore = averageScore;
    }
    
    public List<String> getPhotos() {
        return photos;
    }
    
    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
} 