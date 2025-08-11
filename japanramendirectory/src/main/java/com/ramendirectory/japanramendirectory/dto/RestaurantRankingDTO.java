package com.ramendirectory.japanramendirectory.dto;

import com.ramendirectory.japanramendirectory.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class RestaurantRankingDTO {
    private Long id;
    private String name;
    private Double score;
    private String areaName;
    private String prefectureName;
    private Set<GenreDTO> genres;
    private Set<SoupBase> soupBases;
    private String thumbnailUrl;  // For a small image if available
    
    // Static method to convert Restaurant to RestaurantRankingDTO
    public static RestaurantRankingDTO fromEntity(Restaurant restaurant) {
        RestaurantRankingDTO dto = new RestaurantRankingDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setScore(restaurant.getScore());
        // Convert Genre to GenreDTO
        if (restaurant.getGenres() != null) {
            dto.setGenres(restaurant.getGenres().stream()
                .map(GenreDTO::new)
                .collect(Collectors.toSet()));
        }
        dto.setSoupBases(restaurant.getSoupBases());
        
        if (restaurant.getAddress() != null && restaurant.getAddress().getArea() != null) {
            dto.setAreaName(restaurant.getAddress().getArea().getName());
            
            if (restaurant.getAddress().getArea().getPrefecture() != null) {
                dto.setPrefectureName(restaurant.getAddress().getArea().getPrefecture().getName());
            }
        }
        
        // Placeholder for if you add image support in the future
        // dto.setThumbnailUrl(...);
        
        return dto;
    }
    
    // Static method to convert a list of Restaurants to DTOs
    public static List<RestaurantRankingDTO> fromEntities(List<Restaurant> restaurants) {
        return restaurants.stream()
                .map(RestaurantRankingDTO::fromEntity)
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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getPrefectureName() {
        return prefectureName;
    }

    public void setPrefectureName(String prefectureName) {
        this.prefectureName = prefectureName;
    }

    public Set<GenreDTO> getGenres() {
        return genres;
    }

    public void setGenres(Set<GenreDTO> genres) {
        this.genres = genres;
    }

    public Set<SoupBase> getSoupBases() {
        return soupBases;
    }

    public void setSoupBases(Set<SoupBase> soupBases) {
        this.soupBases = soupBases;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
} 