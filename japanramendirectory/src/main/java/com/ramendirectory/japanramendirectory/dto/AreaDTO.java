package com.ramendirectory.japanramendirectory.dto;

import com.ramendirectory.japanramendirectory.model.Area;

public class AreaDTO {
    private Long id;
    private String name;
    private String nameInEnglish;
    private Long prefectureId;
    private String fullName;
    private String fullNameInEnglish;
    
    public static AreaDTO fromEntity(Area area) {
        AreaDTO dto = new AreaDTO();
        dto.setId(area.getId());
        dto.setName(area.getName());
        dto.setNameInEnglish(area.getNameInEnglish());
        if (area.getPrefecture() != null) {
            dto.setPrefectureId(area.getPrefecture().getId());
        }
        dto.setFullName(area.getFullName());
        dto.setFullNameInEnglish(area.getFullNameInEnglish());
        return dto;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getNameInEnglish() {
        return nameInEnglish;
    }
    
    public void setNameInEnglish(String nameInEnglish) {
        this.nameInEnglish = nameInEnglish;
    }
    
    public Long getPrefectureId() {
        return prefectureId;
    }
    
    public void setPrefectureId(Long prefectureId) {
        this.prefectureId = prefectureId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getFullNameInEnglish() {
        return fullNameInEnglish;
    }
    
    public void setFullNameInEnglish(String fullNameInEnglish) {
        this.fullNameInEnglish = fullNameInEnglish;
    }
} 