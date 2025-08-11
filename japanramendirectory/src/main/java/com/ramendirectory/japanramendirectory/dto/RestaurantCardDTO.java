package com.ramendirectory.japanramendirectory.dto;

import com.ramendirectory.japanramendirectory.model.Restaurant;
import java.util.List;

/**
 * Lightweight DTO for front page restaurant cards
 */
public class RestaurantCardDTO {
    private Long id;
    private String name;
    private String prefectureName;
    private String areaName;
    private Double score;
    
    public static RestaurantCardDTO fromEntity(Restaurant restaurant) {
        RestaurantCardDTO dto = new RestaurantCardDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setScore(restaurant.getScore());
        
        if (restaurant.getAddress() != null) {
            if (restaurant.getAddress().getArea() != null) {
                dto.setAreaName(restaurant.getAddress().getArea().getName());
                
                if (restaurant.getAddress().getArea().getPrefecture() != null) {
                    dto.setPrefectureName(restaurant.getAddress().getArea().getPrefecture().getName());
                }
            }
        }
        
        return dto;
    }
    
    public static List<RestaurantCardDTO> fromEntities(List<Restaurant> restaurants) {
        return restaurants.stream()
                .map(RestaurantCardDTO::fromEntity)
                .toList();
    }
    
    // Getters and setters
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

    public String getPrefectureName() {
        return prefectureName;
    }

    public void setPrefectureName(String prefectureName) {
        this.prefectureName = prefectureName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
} 