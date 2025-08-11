package com.ramendirectory.japanramendirectory.dto;

import com.ramendirectory.japanramendirectory.model.Genre;

public class GenreDTO {
    private String name;
    private String chineseLabel;
    
    public GenreDTO(Genre genre) {
        this.name = genre.name();
        this.chineseLabel = genre.getChineseLabel();
    }
    
    // Empty constructor for Jackson
    public GenreDTO() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getChineseLabel() {
        return chineseLabel;
    }
    
    public void setChineseLabel(String chineseLabel) {
        this.chineseLabel = chineseLabel;
    }
} 