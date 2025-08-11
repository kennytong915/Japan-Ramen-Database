package com.ramendirectory.japanramendirectory.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.ramendirectory.japanramendirectory.model.Area;
import com.ramendirectory.japanramendirectory.model.Genre;
import com.ramendirectory.japanramendirectory.model.Prefecture;
import com.ramendirectory.japanramendirectory.model.Restaurant;
import com.ramendirectory.japanramendirectory.model.SoupBase;
import com.ramendirectory.japanramendirectory.dto.GenreDTO;

public interface RestaurantService {
    // Basic CRUD operations
    Restaurant createRestaurant(Restaurant restaurant);
    List<Restaurant> getAllRestaurants();
    Page<Restaurant> getAllRestaurants(Pageable pageable);
    Optional<Restaurant> getRestaurantById(Long id);
    Optional<Restaurant> updateRestaurant(Long id, Restaurant restaurantDetails);
    boolean deleteRestaurant(Long id);
    
    // Specialized methods
    List<Restaurant> findByArea(Area area);
    Page<Restaurant> findByArea(Area area, Pageable pageable);
    List<Restaurant> findByAreaId(Long areaId);
    Page<Restaurant> findByAreaId(Long areaId, Pageable pageable);
    List<Restaurant> findByPrefecture(Prefecture prefecture);
    Page<Restaurant> findByPrefecture(Prefecture prefecture, Pageable pageable);
    List<Restaurant> findByPrefectureId(Long prefectureId);
    Page<Restaurant> findByPrefectureId(Long prefectureId, Pageable pageable);
    List<Restaurant> findBySoupBase(SoupBase soupBase);
    Page<Restaurant> findBySoupBase(SoupBase soupBase, Pageable pageable);
    List<Restaurant> findByScoreGreaterThanEqual(Double score);
    Page<Restaurant> findByScoreGreaterThanEqual(Double score, Pageable pageable);
    
    // Search restaurants by name
    List<Restaurant> findByNameContaining(String name);
    Page<Restaurant> findByNameContaining(String name, Pageable pageable);
    
    // Updated method to use Genre enum
    List<Restaurant> findByGenre(Genre genre);
    Page<Restaurant> findByGenre(Genre genre, Pageable pageable);
    
    // Filter method for the ranking page
    List<Restaurant> findWithFilters(
        Long prefectureId, 
        Long areaId,
        List<Genre> genres,
        List<SoupBase> soupBases,
        Double minScore,
        String name,
        String sortBy,
        Sort.Direction sortDirection,
        Pageable pageable
    );
    
    // Filter method for the ranking page with pagination
    Page<Restaurant> findWithFiltersPage(
        Long prefectureId, 
        Long areaId,
        List<Genre> genres,
        List<SoupBase> soupBases,
        Double minScore,
        String name,
        String sortBy,
        Sort.Direction sortDirection,
        Pageable pageable
    );
    
    // Utility methods
    List<Restaurant> findTopRatedRestaurants(int limit);
    List<Area> getAllAreas();
    List<Prefecture> getAllPrefectures();
    
    // Updated to return Genre enums
    List<Genre> getAllGenres();
    
    // New method to return GenreDTO with Chinese labels
    List<GenreDTO> getAllGenresWithLabels();
    
    List<SoupBase> getAllSoupBases();
    
    // Legacy method - deprecated
    @Deprecated
    List<Restaurant> findByGenre(String genre);
    @Deprecated
    Page<Restaurant> findByGenre(String genre, Pageable pageable);
}
