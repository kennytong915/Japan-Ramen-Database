package com.ramendirectory.japanramendirectory.dto;

import com.ramendirectory.japanramendirectory.model.QueueMethod;
import com.ramendirectory.japanramendirectory.model.QueueType;

public class QueueMethodDTO {
    private Long id;
    private QueueType type;
    private String detailedGuide;
    private String chineseLabel;

    // Static method to convert QueueMethod to QueueMethodDTO
    public static QueueMethodDTO fromEntity(QueueMethod queueMethod) {
        if (queueMethod == null) {
            return null;
        }
        
        QueueMethodDTO dto = new QueueMethodDTO();
        dto.setId(queueMethod.getId());
        dto.setType(queueMethod.getType());
        dto.setDetailedGuide(queueMethod.getDetailedGuide());
        
        if (queueMethod.getType() != null) {
            dto.setChineseLabel(queueMethod.getType().getChineseLabel());
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

    public QueueType getType() {
        return type;
    }

    public void setType(QueueType type) {
        this.type = type;
    }

    public String getDetailedGuide() {
        return detailedGuide;
    }

    public void setDetailedGuide(String detailedGuide) {
        this.detailedGuide = detailedGuide;
    }

    public String getChineseLabel() {
        return chineseLabel;
    }

    public void setChineseLabel(String chineseLabel) {
        this.chineseLabel = chineseLabel;
    }
} 