package com.ramendirectory.japanramendirectory.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommentRequestDTO {
    
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;
    
    @NotBlank(message = "Food comment is required")
    @Size(min = 2, max = 1000, message = "Food comment must be between 2 and 1000 characters")
    private String foodComment;
    
    @NotBlank(message = "Ease of visiting comment is required")
    @Size(min = 2, max = 1000, message = "Ease of visiting comment must be between 2 and 1000 characters")
    private String visitingComment;
    
    @NotBlank(message = "Environment and service comment is required")
    @Size(min = 2, max = 1000, message = "Environment and service comment must be between 2 and 1000 characters")
    private String environmentComment;
    
    @NotNull(message = "Food score is required")
    @Min(value = 1, message = "Food score must be at least 1")
    @Max(value = 5, message = "Food score must be at most 5")
    private Integer foodScore;
    
    @NotNull(message = "Ease of visiting score is required")
    @Min(value = 1, message = "Ease of visiting score must be at least 1")
    @Max(value = 5, message = "Ease of visiting score must be at most 5")
    private Integer visitingScore;
    
    @NotNull(message = "Environment and service score is required")
    @Min(value = 1, message = "Environment and service score must be at least 1")
    @Max(value = 5, message = "Environment and service score must be at most 5")
    private Integer environmentScore;
    
    @NotNull(message = "Overall score is required")
    @Min(value = 1, message = "Overall score must be at least 1")
    @Max(value = 5, message = "Overall score must be at most 5")
    private Integer overallScore;
    
    // Default constructor
    public CommentRequestDTO() {
    }
    
    // Getters and setters
    public Long getRestaurantId() {
        return restaurantId;
    }
    
    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
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
} 