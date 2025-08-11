package com.ramendirectory.japanramendirectory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ramendirectory.japanramendirectory.model.Area;
import com.ramendirectory.japanramendirectory.model.Genre;
import com.ramendirectory.japanramendirectory.model.Prefecture;
import com.ramendirectory.japanramendirectory.model.Restaurant;
import com.ramendirectory.japanramendirectory.model.SoupBase;
import com.ramendirectory.japanramendirectory.repository.RestaurantRepository;
import com.ramendirectory.japanramendirectory.repository.RestaurantSpecification;
import com.ramendirectory.japanramendirectory.dto.GenreDTO;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    
    private final RestaurantRepository restaurantRepository;
    
    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }
    
    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }
    
    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }
    
    @Override
    public Page<Restaurant> getAllRestaurants(Pageable pageable) {
        return restaurantRepository.findAll(pageable);
    }
    
    @Override
    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }
    
    @Override
    public Optional<Restaurant> updateRestaurant(Long id, Restaurant restaurantDetails) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(id);
        if (restaurantOptional.isPresent()) {
            Restaurant restaurant = restaurantOptional.get();
            restaurant.setName(restaurantDetails.getName());
            restaurant.setGenres(restaurantDetails.getGenres());
            restaurant.setSoupBases(restaurantDetails.getSoupBases());
            restaurant.setScore(restaurantDetails.getScore());
            restaurant.setReservationSystem(restaurantDetails.getReservationSystem());
            restaurant.setQueueMethod(restaurantDetails.getQueueMethod());
            restaurant.setAddress(restaurantDetails.getAddress());
            restaurant.setSeats(restaurantDetails.getSeats());
            restaurant.setSocialMediaLinks(restaurantDetails.getSocialMediaLinks());
            return Optional.of(restaurantRepository.save(restaurant));
        }
        return Optional.empty();
    }
    
    @Override
    public boolean deleteRestaurant(Long id) {
        if (restaurantRepository.existsById(id)) {
            restaurantRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    @Override
    public List<Restaurant> findByArea(Area area) {
        return restaurantRepository.findByArea(area);
    }
    
    @Override
    public Page<Restaurant> findByArea(Area area, Pageable pageable) {
        return restaurantRepository.findByArea(area, pageable);
    }
    
    @Override
    public List<Restaurant> findByAreaId(Long areaId) {
        return restaurantRepository.findByAreaId(areaId);
    }
    
    @Override
    public Page<Restaurant> findByAreaId(Long areaId, Pageable pageable) {
        return restaurantRepository.findByAreaId(areaId, pageable);
    }
    
    @Override
    public List<Restaurant> findByPrefecture(Prefecture prefecture) {
        return restaurantRepository.findByPrefecture(prefecture);
    }
    
    @Override
    public Page<Restaurant> findByPrefecture(Prefecture prefecture, Pageable pageable) {
        return restaurantRepository.findByPrefecture(prefecture, pageable);
    }
    
    @Override
    public List<Restaurant> findByPrefectureId(Long prefectureId) {
        return restaurantRepository.findByPrefectureId(prefectureId);
    }
    
    @Override
    public Page<Restaurant> findByPrefectureId(Long prefectureId, Pageable pageable) {
        return restaurantRepository.findByPrefectureId(prefectureId, pageable);
    }
    
    @Override
    public List<Restaurant> findBySoupBase(SoupBase soupBase) {
        return restaurantRepository.findBySoupBasesContaining(soupBase);
    }
    
    @Override
    public Page<Restaurant> findBySoupBase(SoupBase soupBase, Pageable pageable) {
        return restaurantRepository.findBySoupBasesContaining(soupBase, pageable);
    }
    
    @Override
    public List<Restaurant> findByScoreGreaterThanEqual(Double score) {
        return restaurantRepository.findByScoreGreaterThanEqual(score);
    }
    
    @Override
    public Page<Restaurant> findByScoreGreaterThanEqual(Double score, Pageable pageable) {
        return restaurantRepository.findByScoreGreaterThanEqual(score, pageable);
    }
    
    @Override
    public List<Restaurant> findByGenre(Genre genre) {
        return restaurantRepository.findByGenre(genre);
    }
    
    @Override
    public Page<Restaurant> findByGenre(Genre genre, Pageable pageable) {
        return restaurantRepository.findByGenre(genre, pageable);
    }
    
    @Override
    @Deprecated
    public List<Restaurant> findByGenre(String genre) {
        return restaurantRepository.findByGenresContainingIgnoreCase(genre);
    }
    
    @Override
    @Deprecated
    public Page<Restaurant> findByGenre(String genre, Pageable pageable) {
        return restaurantRepository.findByGenresContainingIgnoreCase(genre, pageable);
    }
    
    @Override
    public List<Restaurant> findTopRatedRestaurants(int limit) {
        return restaurantRepository.findByOrderByScoreDesc(PageRequest.of(0, limit));
    }
    
    @Override
    public List<Area> getAllAreas() {
        return restaurantRepository.findAllAreas();
    }
    
    @Override
    public List<Prefecture> getAllPrefectures() {
        return restaurantRepository.findAllPrefectures();
    }
    
    @Override
    public List<Genre> getAllGenres() {
        return restaurantRepository.findAllDistinctGenres();
    }
    
    @Override
    public List<GenreDTO> getAllGenresWithLabels() {
        List<Genre> genres = getAllGenres();
        return genres.stream()
                .map(genre -> new GenreDTO(genre))
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<SoupBase> getAllSoupBases() {
        return restaurantRepository.findAllSoupBases();
    }
    
    @Override
    public List<Restaurant> findWithFilters(
            Long prefectureId, 
            Long areaId,
            List<Genre> genres,
            List<SoupBase> soupBases,
            Double minScore,
            String name,
            String sortBy,
            Sort.Direction sortDirection,
            Pageable pageable) {
        
        // Build specifications
        Specification<Restaurant> spec = Specification.where(RestaurantSpecification.distinct());
        
        if (prefectureId != null) {
            spec = spec.and(RestaurantSpecification.hasPrefecture(prefectureId));
        }
        
        if (areaId != null) {
            spec = spec.and(RestaurantSpecification.hasArea(areaId));
        }
        
        if (genres != null && !genres.isEmpty()) {
            spec = spec.and(RestaurantSpecification.hasAnyGenre(genres));
        }
        
        if (soupBases != null && !soupBases.isEmpty()) {
            spec = spec.and(RestaurantSpecification.hasAnySoupBase(soupBases));
        }
        
        if (minScore != null) {
            spec = spec.and(RestaurantSpecification.hasMinScore(minScore));
        }
        
        if (name != null && !name.trim().isEmpty()) {
            spec = spec.and(RestaurantSpecification.hasNameContaining(name));
        }
        
        // Determine sorting
        Sort sort;
        if (sortBy == null) {
            sort = Sort.by(Sort.Direction.DESC, "score");
        } else {
            Sort.Direction direction = sortDirection != null ? sortDirection : Sort.Direction.DESC;
            switch (sortBy) {
                case "name":
                    sort = Sort.by(direction, "name");
                    break;
                default:
                    sort = Sort.by(direction, "score");
            }
        }
        
        // Apply pagination if provided
        if (pageable != null) {
            return restaurantRepository.findAll(spec, 
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort))
                    .getContent();
        }
        
        // Otherwise return all results with sorting
        return restaurantRepository.findAll(spec, sort);
    }
    
    @Override
    public Page<Restaurant> findWithFiltersPage(
            Long prefectureId, 
            Long areaId,
            List<Genre> genres,
            List<SoupBase> soupBases,
            Double minScore,
            String name,
            String sortBy,
            Sort.Direction sortDirection,
            Pageable pageable) {
        
        // Build specifications
        Specification<Restaurant> spec = Specification.where(RestaurantSpecification.distinct());
        
        if (prefectureId != null) {
            spec = spec.and(RestaurantSpecification.hasPrefecture(prefectureId));
        }
        
        if (areaId != null) {
            spec = spec.and(RestaurantSpecification.hasArea(areaId));
        }
        
        if (genres != null && !genres.isEmpty()) {
            spec = spec.and(RestaurantSpecification.hasAnyGenre(genres));
        }
        
        if (soupBases != null && !soupBases.isEmpty()) {
            spec = spec.and(RestaurantSpecification.hasAnySoupBase(soupBases));
        }
        
        if (minScore != null) {
            spec = spec.and(RestaurantSpecification.hasMinScore(minScore));
        }
        
        if (name != null && !name.trim().isEmpty()) {
            spec = spec.and(RestaurantSpecification.hasNameContaining(name));
        }
        
        // Determine sorting
        Sort sort;
        if (sortBy == null) {
            sort = Sort.by(Sort.Direction.DESC, "score");
        } else {
            Sort.Direction direction = sortDirection != null ? sortDirection : Sort.Direction.DESC;
            switch (sortBy) {
                case "name":
                    sort = Sort.by(direction, "name");
                    break;
                default:
                    sort = Sort.by(direction, "score");
            }
        }
        
        // Apply pagination with sorting
        return restaurantRepository.findAll(spec, 
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort));
    }
    
    @Override
    public List<Restaurant> findByNameContaining(String name) {
        return restaurantRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    public Page<Restaurant> findByNameContaining(String name, Pageable pageable) {
        return restaurantRepository.findByNameContainingIgnoreCase(name, pageable);
    }
} 