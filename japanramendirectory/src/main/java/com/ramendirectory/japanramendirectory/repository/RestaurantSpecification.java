package com.ramendirectory.japanramendirectory.repository;

import com.ramendirectory.japanramendirectory.model.*;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;

import java.util.List;

public class RestaurantSpecification {
    
    public static Specification<Restaurant> hasArea(Long areaId) {
        return (root, query, criteriaBuilder) -> {
            if (areaId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Restaurant, Address> addressJoin = root.join("address", JoinType.INNER);
            Join<Address, Area> areaJoin = addressJoin.join("area", JoinType.INNER);
            return criteriaBuilder.equal(areaJoin.get("id"), areaId);
        };
    }
    
    public static Specification<Restaurant> hasPrefecture(Long prefectureId) {
        return (root, query, criteriaBuilder) -> {
            if (prefectureId == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Restaurant, Address> addressJoin = root.join("address", JoinType.INNER);
            Join<Address, Area> areaJoin = addressJoin.join("area", JoinType.INNER);
            Join<Area, Prefecture> prefectureJoin = areaJoin.join("prefecture", JoinType.INNER);
            return criteriaBuilder.equal(prefectureJoin.get("id"), prefectureId);
        };
    }
    
    public static Specification<Restaurant> hasAnyGenre(List<Genre> genres) {
        return (root, query, criteriaBuilder) -> {
            if (genres == null || genres.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            Join<Restaurant, Genre> genreJoin = root.join("genres", JoinType.INNER);
            return genreJoin.in(genres);
        };
    }
    
    public static Specification<Restaurant> hasAnySoupBase(List<SoupBase> soupBases) {
        return (root, query, criteriaBuilder) -> {
            if (soupBases == null || soupBases.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            Join<Restaurant, SoupBase> soupBaseJoin = root.join("soupBases", JoinType.INNER);
            return soupBaseJoin.in(soupBases);
        };
    }
    
    public static Specification<Restaurant> hasMinScore(Double minScore) {
        return (root, query, criteriaBuilder) -> 
            minScore == null ? criteriaBuilder.conjunction() : 
                criteriaBuilder.greaterThanOrEqualTo(root.get("score"), minScore);
    }
    
    public static Specification<Restaurant> hasReservation(Boolean reservationSystem) {
        return (root, query, criteriaBuilder) -> 
            reservationSystem == null ? criteriaBuilder.conjunction() : 
                criteriaBuilder.equal(root.get("reservationSystem"), reservationSystem);
    }
    
    public static Specification<Restaurant> hasNameContaining(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")), 
                "%" + name.toLowerCase() + "%"
            );
        };
    }
    
    /**
     * Distinct is needed when querying with joins to avoid duplicate results
     */
    public static Specification<Restaurant> distinct() {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.conjunction();
        };
    }
} 