package com.ramendirectory.japanramendirectory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ramendirectory.japanramendirectory.dto.PageResponse;
import com.ramendirectory.japanramendirectory.dto.RestaurantRankingDTO;
import com.ramendirectory.japanramendirectory.model.Genre;
import com.ramendirectory.japanramendirectory.model.Restaurant;
import com.ramendirectory.japanramendirectory.model.SoupBase;
import com.ramendirectory.japanramendirectory.service.RestaurantService;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RestaurantService restaurantService;
    
    @Autowired
    public RankingController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }
    
    @GetMapping
    public ResponseEntity<?> getRankedRestaurants(
            @RequestParam(required = false) Long prefectureId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) List<Genre> genres,
            @RequestParam(required = false) List<SoupBase> soupBases,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "score") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "false") boolean paginated) {
        
        Pageable pageable = PageRequest.of(page, size);
        
        if (paginated) {
            Page<Restaurant> restaurantPage = restaurantService.findWithFiltersPage(
                    prefectureId,
                    areaId,
                    genres,
                    soupBases,
                    minScore,
                    name,
                    sortBy,
                    sortDirection,
                    pageable
            );
            
            return ResponseEntity.ok(
                    PageResponse.of(restaurantPage, RestaurantRankingDTO.fromEntities(restaurantPage.getContent())));
        } else {
            List<Restaurant> restaurants = restaurantService.findWithFilters(
                    prefectureId,
                    areaId,
                    genres,
                    soupBases,
                    minScore,
                    name,
                    sortBy,
                    sortDirection,
                    pageable
            );
            
            return ResponseEntity.ok(RestaurantRankingDTO.fromEntities(restaurants));
        }
    }
    
    @GetMapping("/top")
    public ResponseEntity<List<RestaurantRankingDTO>> getTopRatedRestaurants(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Restaurant> restaurants = restaurantService.findTopRatedRestaurants(limit);
        return ResponseEntity.ok(RestaurantRankingDTO.fromEntities(restaurants));
    }
    
} 