package com.ramendirectory.japanramendirectory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReportCommentDTO {
    
    @NotNull(message = "Comment ID is required")
    private Long commentId;
    
    @NotBlank(message = "Report reason is required")
    @Size(min = 5, max = 1000, message = "Report reason must be between 5 and 1000 characters")
    private String reason;
    
    // Default constructor
    public ReportCommentDTO() {
    }
    
    // Constructor with parameters
    public ReportCommentDTO(Long commentId, String reason) {
        this.commentId = commentId;
        this.reason = reason;
    }
    
    // Getters and setters
    public Long getCommentId() {
        return commentId;
    }
    
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
} 