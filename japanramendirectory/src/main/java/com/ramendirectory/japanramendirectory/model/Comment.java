package com.ramendirectory.japanramendirectory.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @NotBlank(message = "Food comment is required")
    @Size(min = 2, max = 1000, message = "Food comment must be between 2 and 1000 characters")
    @Column(nullable = false, length = 1000)
    private String foodComment;

    @NotBlank(message = "Ease of visiting comment is required")
    @Size(min = 2, max = 1000, message = "Ease of visiting comment must be between 2 and 1000 characters")
    @Column(nullable = false, length = 1000)
    private String visitingComment;

    @NotBlank(message = "Environment and service comment is required")
    @Size(min = 2, max = 1000, message = "Environment and service comment must be between 2 and 1000 characters")
    @Column(nullable = false, length = 1000)
    private String environmentComment;

    @NotNull(message = "Food score is required")
    @Min(value = 1, message = "Food score must be at least 1")
    @Max(value = 5, message = "Food score must be at most 5")
    @Column(nullable = false)
    private Integer foodScore;

    @NotNull(message = "Ease of visiting score is required")
    @Min(value = 1, message = "Ease of visiting score must be at least 1")
    @Max(value = 5, message = "Ease of visiting score must be at most 5")
    @Column(nullable = false)
    private Integer visitingScore;

    @NotNull(message = "Environment and service score is required")
    @Min(value = 1, message = "Environment and service score must be at least 1")
    @Max(value = 5, message = "Environment and service score must be at most 5")
    @Column(nullable = false)
    private Integer environmentScore;

    @NotNull(message = "Overall score is required")
    @Min(value = 1, message = "Overall score must be at least 1")
    @Max(value = 5, message = "Overall score must be at most 5")
    @Column(nullable = false)
    private Integer overallScore;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean reported = false;

    @Column(nullable = true, length = 1000)
    private String reportReason;

    @Column(nullable = false)
    private boolean approved = true;

    @Column(nullable = true)
    private LocalDateTime reportedAt;
    
    @ElementCollection
    @CollectionTable(name = "comment_photos", joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "photo_url", length = 1000)
    private List<String> photos = new ArrayList<>();

    // Default constructor
    public Comment() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
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

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }

    public String getReportReason() {
        return reportReason;
    }

    public void setReportReason(String reportReason) {
        this.reportReason = reportReason;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }
    
    public List<String> getPhotos() {
        return photos;
    }
    
    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
    
    // Add a photo URL to the comment
    public void addPhoto(String photoUrl) {
        if (this.photos == null) {
            this.photos = new ArrayList<>();
        }
        this.photos.add(photoUrl);
    }
    
    // Calculate the average score
    public float getAverageScore() {
        return (foodScore + visitingScore + environmentScore) / 3.0f;
    }
} 