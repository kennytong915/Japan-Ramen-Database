package com.ramendirectory.japanramendirectory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ramendirectory.japanramendirectory.dto.PageResponse;
import com.ramendirectory.japanramendirectory.dto.RestaurantDTO;
import com.ramendirectory.japanramendirectory.dto.RestaurantCardDTO;
import com.ramendirectory.japanramendirectory.dto.GenreDTO;
import com.ramendirectory.japanramendirectory.dto.AreaDTO;
import com.ramendirectory.japanramendirectory.model.Area;
import com.ramendirectory.japanramendirectory.model.Genre;
import com.ramendirectory.japanramendirectory.model.Prefecture;
import com.ramendirectory.japanramendirectory.model.Restaurant;
import com.ramendirectory.japanramendirectory.model.SoupBase;
import com.ramendirectory.japanramendirectory.service.RestaurantService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    
    private final RestaurantService restaurantService;
    
    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }
    
    @PostMapping
    public ResponseEntity<RestaurantDTO> createRestaurant(@RequestBody Restaurant restaurant) {
        Restaurant newRestaurant = restaurantService.createRestaurant(restaurant);
        return new ResponseEntity<>(RestaurantDTO.fromEntity(newRestaurant), HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllRestaurants(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "false") boolean paginated) {
        
        if (paginated) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "score"));
            
            // If name parameter is provided, search by name
            if (name != null && !name.trim().isEmpty()) {
                Page<Restaurant> restaurantPage = restaurantService.findByNameContaining(name, pageable);
                return new ResponseEntity<>(
                    PageResponse.of(restaurantPage, RestaurantDTO.fromEntities(restaurantPage.getContent())),
                    HttpStatus.OK
                );
            } else {
                // Otherwise return all restaurants
                Page<Restaurant> restaurantPage = restaurantService.getAllRestaurants(pageable);
                return new ResponseEntity<>(
                    PageResponse.of(restaurantPage, RestaurantDTO.fromEntities(restaurantPage.getContent())),
                    HttpStatus.OK
                );
            }
        } else {
            // Handle non-paginated requests
            if (name != null && !name.trim().isEmpty()) {
                List<Restaurant> restaurants = restaurantService.findByNameContaining(name);
                return new ResponseEntity<>(RestaurantDTO.fromEntities(restaurants), HttpStatus.OK);
            } else {
                List<Restaurant> restaurants = restaurantService.getAllRestaurants();
                return new ResponseEntity<>(RestaurantDTO.fromEntities(restaurants), HttpStatus.OK);
            }
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable Long id) {
        Optional<Restaurant> restaurant = restaurantService.getRestaurantById(id);
        return restaurant.map(r -> new ResponseEntity<>(RestaurantDTO.fromEntity(r), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(@PathVariable Long id, @RequestBody Restaurant restaurant) {
        Optional<Restaurant> updatedRestaurant = restaurantService.updateRestaurant(id, restaurant);
        return updatedRestaurant.map(r -> new ResponseEntity<>(RestaurantDTO.fromEntity(r), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        boolean deleted = restaurantService.deleteRestaurant(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/area/{areaId}")
    public ResponseEntity<?> getRestaurantsByArea(
            @PathVariable Long areaId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "false") boolean paginated) {
        
        if (paginated) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "score"));
            Page<Restaurant> restaurantPage = restaurantService.findByAreaId(areaId, pageable);
            return new ResponseEntity<>(
                PageResponse.of(restaurantPage, RestaurantDTO.fromEntities(restaurantPage.getContent())),
                HttpStatus.OK
            );
        } else {
            List<Restaurant> restaurants = restaurantService.findByAreaId(areaId);
            return new ResponseEntity<>(RestaurantDTO.fromEntities(restaurants), HttpStatus.OK);
        }
    }
    
    @GetMapping("/prefecture/{prefectureId}")
    public ResponseEntity<?> getRestaurantsByPrefecture(
            @PathVariable Long prefectureId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "false") boolean paginated) {
        
        if (paginated) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "score"));
            Page<Restaurant> restaurantPage = restaurantService.findByPrefectureId(prefectureId, pageable);
            return new ResponseEntity<>(
                PageResponse.of(restaurantPage, RestaurantDTO.fromEntities(restaurantPage.getContent())),
                HttpStatus.OK
            );
        } else {
            List<Restaurant> restaurants = restaurantService.findByPrefectureId(prefectureId);
            return new ResponseEntity<>(RestaurantDTO.fromEntities(restaurants), HttpStatus.OK);
        }
    }
    
    @GetMapping("/soupBase/{soupBase}")
    public ResponseEntity<?> getRestaurantsBySoupBase(
            @PathVariable SoupBase soupBase,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "false") boolean paginated) {
        
        if (paginated) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "score"));
            Page<Restaurant> restaurantPage = restaurantService.findBySoupBase(soupBase, pageable);
            return new ResponseEntity<>(
                PageResponse.of(restaurantPage, RestaurantDTO.fromEntities(restaurantPage.getContent())),
                HttpStatus.OK
            );
        } else {
            List<Restaurant> restaurants = restaurantService.findBySoupBase(soupBase);
            return new ResponseEntity<>(RestaurantDTO.fromEntities(restaurants), HttpStatus.OK);
        }
    }
    
    @GetMapping("/score/{score}")
    public ResponseEntity<?> getRestaurantsByScore(
            @PathVariable Double score,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "false") boolean paginated) {
        
        if (paginated) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "score"));
            Page<Restaurant> restaurantPage = restaurantService.findByScoreGreaterThanEqual(score, pageable);
            return new ResponseEntity<>(
                PageResponse.of(restaurantPage, RestaurantDTO.fromEntities(restaurantPage.getContent())),
                HttpStatus.OK
            );
        } else {
            List<Restaurant> restaurants = restaurantService.findByScoreGreaterThanEqual(score);
            return new ResponseEntity<>(RestaurantDTO.fromEntities(restaurants), HttpStatus.OK);
        }
    }
    
    @GetMapping("/genre/{genre}")
    public ResponseEntity<?> getRestaurantsByGenre(
            @PathVariable String genre,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "false") boolean paginated) {
        
        if (paginated) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "score"));
            Page<Restaurant> restaurantPage = restaurantService.findByGenre(genre, pageable);
            return new ResponseEntity<>(
                PageResponse.of(restaurantPage, RestaurantDTO.fromEntities(restaurantPage.getContent())),
                HttpStatus.OK
            );
        } else {
            List<Restaurant> restaurants = restaurantService.findByGenre(genre);
            return new ResponseEntity<>(RestaurantDTO.fromEntities(restaurants), HttpStatus.OK);
        }
    }
    
    @GetMapping("/top/{limit}")
    public ResponseEntity<List<RestaurantDTO>> getTopRatedRestaurants(@PathVariable int limit) {
        List<Restaurant> restaurants = restaurantService.findTopRatedRestaurants(limit);
        return new ResponseEntity<>(RestaurantDTO.fromEntities(restaurants), HttpStatus.OK);
    }
    
    @GetMapping("/areas")
    public ResponseEntity<List<AreaDTO>> getAllAreas() {
        List<Area> areas = restaurantService.getAllAreas();
        List<AreaDTO> areaDTOs = areas.stream()
            .map(AreaDTO::fromEntity)
            .collect(java.util.stream.Collectors.toList());
        return new ResponseEntity<>(areaDTOs, HttpStatus.OK);
    }
    
    @GetMapping("/prefectures")
    public ResponseEntity<List<Prefecture>> getAllPrefectures() {
        List<Prefecture> prefectures = restaurantService.getAllPrefectures();
        return new ResponseEntity<>(prefectures, HttpStatus.OK);
    }
    
    @GetMapping("/genres")
    public ResponseEntity<List<GenreDTO>> getAllGenres() {
        List<GenreDTO> genres = restaurantService.getAllGenresWithLabels();
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }
    
    @GetMapping("/soupbases")
    public ResponseEntity<List<SoupBase>> getAllSoupBases() {
        List<SoupBase> soupBases = restaurantService.getAllSoupBases();
        return new ResponseEntity<>(soupBases, HttpStatus.OK);
    }
    
    /**
     * Get top 5 restaurants for front page cards
     */
    @GetMapping("/frontpage-cards")
    public ResponseEntity<List<RestaurantCardDTO>> getFrontPageCards() {
        List<Restaurant> topRestaurants = restaurantService.findTopRatedRestaurants(5);
        return new ResponseEntity<>(RestaurantCardDTO.fromEntities(topRestaurants), HttpStatus.OK);
    }
}
