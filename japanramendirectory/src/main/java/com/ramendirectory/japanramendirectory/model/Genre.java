package com.ramendirectory.japanramendirectory.model;

public enum Genre {
    RAMEN("拉麵"),
    TSUKEMEN("沾麵"),
    VEGETARIAN("素食拉麵"),
    TANTANMEN("擔擔麵"),
    OTHER("其他");
    
    private final String chineseLabel;
    
    Genre(String chineseLabel) {
        this.chineseLabel = chineseLabel;
    }
    
    public String getChineseLabel() {
        return chineseLabel;
    }
} 