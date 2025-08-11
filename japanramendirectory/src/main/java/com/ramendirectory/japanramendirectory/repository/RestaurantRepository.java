package com.ramendirectory.japanramendirectory.repository;

import com.ramendirectory.japanramendirectory.model.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, JpaSpecificationExecutor<Restaurant> {
    
    @Query("SELECT r FROM Restaurant r JOIN r.address a WHERE a.area = :area")
    List<Restaurant> findByArea(@Param("area") Area area);
    
    @Query("SELECT r FROM Restaurant r JOIN r.address a WHERE a.area = :area")
    Page<Restaurant> findByArea(@Param("area") Area area, Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r JOIN r.address a WHERE a.area.id = :areaId")
    List<Restaurant> findByAreaId(@Param("areaId") Long areaId);
    
    @Query("SELECT r FROM Restaurant r JOIN r.address a WHERE a.area.id = :areaId")
    Page<Restaurant> findByAreaId(@Param("areaId") Long areaId, Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r JOIN r.address a JOIN a.area ar WHERE ar.prefecture = :prefecture")
    List<Restaurant> findByPrefecture(@Param("prefecture") Prefecture prefecture);
    
    @Query("SELECT r FROM Restaurant r JOIN r.address a JOIN a.area ar WHERE ar.prefecture = :prefecture")
    Page<Restaurant> findByPrefecture(@Param("prefecture") Prefecture prefecture, Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r JOIN r.address a JOIN a.area ar WHERE ar.prefecture.id = :prefectureId")
    List<Restaurant> findByPrefectureId(@Param("prefectureId") Long prefectureId);
    
    @Query("SELECT r FROM Restaurant r JOIN r.address a JOIN a.area ar WHERE ar.prefecture.id = :prefectureId")
    Page<Restaurant> findByPrefectureId(@Param("prefectureId") Long prefectureId, Pageable pageable);
    
    List<Restaurant> findBySoupBasesContaining(SoupBase soupBase);
    Page<Restaurant> findBySoupBasesContaining(SoupBase soupBase, Pageable pageable);
    
    List<Restaurant> findByScoreGreaterThanEqual(Double score);
    Page<Restaurant> findByScoreGreaterThanEqual(Double score, Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r JOIN r.genres g WHERE g = :genre")
    List<Restaurant> findByGenre(@Param("genre") Genre genre);
    
    @Query("SELECT r FROM Restaurant r JOIN r.genres g WHERE g = :genre")
    Page<Restaurant> findByGenre(@Param("genre") Genre genre, Pageable pageable);
    
    // Methods to find restaurants by name containing the search term
    List<Restaurant> findByNameContainingIgnoreCase(String name);
    Page<Restaurant> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    @Query("SELECT DISTINCT g FROM Restaurant r JOIN r.genres g")
    List<Genre> findAllDistinctGenres();
    
    // Changing this query since genres are now enums
    @Deprecated
    List<Restaurant> findByGenresContainingIgnoreCase(String genre);
    
    @Deprecated
    Page<Restaurant> findByGenresContainingIgnoreCase(String genre, Pageable pageable);
    
    List<Restaurant> findByOrderByScoreDesc(Pageable pageable);
    
    @Query("SELECT DISTINCT a FROM Restaurant r JOIN r.address ad JOIN ad.area a")
    List<Area> findAllAreas();
    
    @Query("SELECT DISTINCT p FROM Restaurant r JOIN r.address a JOIN a.area ar JOIN ar.prefecture p")
    List<Prefecture> findAllPrefectures();
    
    @Deprecated
    @Query("SELECT DISTINCT g FROM Restaurant r JOIN r.genres g")
    List<String> findAllGenres();
    
    @Query("SELECT DISTINCT s FROM Restaurant r JOIN r.soupBases s")
    List<SoupBase> findAllSoupBases();
}
