package com.ramendirectory.japanramendirectory.dto;

import com.ramendirectory.japanramendirectory.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class RestaurantDTO {
    private Long id;
    private String name;
    private Set<GenreDTO> genres;
    private Set<SoupBase> soupBases;
    private Double score;
    private Boolean reservationSystem;
    private QueueMethodDTO queueMethod;
    private Address address;
    private Integer seats;
    private Map<String, String> socialMediaLinks;
    private String menuContent;
    private String openingHours;
    private String restDay;
    private Date openingDate;
    private String description;
    
    // Static method to convert Restaurant to RestaurantDTO
    public static RestaurantDTO fromEntity(Restaurant restaurant) {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        // Convert Genre to GenreDTO
        if (restaurant.getGenres() != null) {
            dto.setGenres(restaurant.getGenres().stream()
                .map(GenreDTO::new)
                .collect(Collectors.toSet()));
        }
        dto.setSoupBases(restaurant.getSoupBases());
        dto.setScore(restaurant.getScore());
        dto.setReservationSystem(restaurant.getReservationSystem());
        dto.setOpeningHours(restaurant.getOpeningHours());
        dto.setRestDay(restaurant.getRestDay());
        dto.setOpeningDate(restaurant.getOpeningDate());
        
        if (restaurant.getQueueMethod() != null) {
            dto.setQueueMethod(QueueMethodDTO.fromEntity(restaurant.getQueueMethod()));
        }
        
        dto.setAddress(restaurant.getAddress());
        dto.setSeats(restaurant.getSeats());
        dto.setSocialMediaLinks(restaurant.getSocialMediaLinks());
        
        if (restaurant.getMenu() != null) {
            dto.setMenuContent(restaurant.getMenu().getMenuContent());
        }
        
        if (restaurant.getDescription() != null) {
            dto.setDescription(restaurant.getDescription().getContent());
        }
        
        return dto;
    }
    
    // Static method to convert a list of Restaurants to DTOs
    public static List<RestaurantDTO> fromEntities(List<Restaurant> restaurants) {
        return restaurants.stream()
                .map(RestaurantDTO::fromEntity)
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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Boolean getReservationSystem() {
        return reservationSystem;
    }

    public void setReservationSystem(Boolean reservationSystem) {
        this.reservationSystem = reservationSystem;
    }

    public QueueMethodDTO getQueueMethod() {
        return queueMethod;
    }

    public void setQueueMethod(QueueMethodDTO queueMethod) {
        this.queueMethod = queueMethod;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public Map<String, String> getSocialMediaLinks() {
        return socialMediaLinks;
    }

    public void setSocialMediaLinks(Map<String, String> socialMediaLinks) {
        this.socialMediaLinks = socialMediaLinks;
    }

    public String getMenuContent() {
        return menuContent;
    }

    public void setMenuContent(String menuContent) {
        this.menuContent = menuContent;
    }
    
    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }
    
    public String getRestDay() {
        return restDay;
    }

    public void setRestDay(String restDay) {
        this.restDay = restDay;
    }

    public Date getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Date openingDate) {
        this.openingDate = openingDate;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
} 