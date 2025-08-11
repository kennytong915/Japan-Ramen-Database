package com.ramendirectory.japanramendirectory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ramendirectory.japanramendirectory.model.Area;
import com.ramendirectory.japanramendirectory.model.Prefecture;

import java.util.List;
import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {
    List<Area> findByPrefecture(Prefecture prefecture);
    List<Area> findByPrefectureId(Long prefectureId);
    Optional<Area> findByNameAndPrefecture(String name, Prefecture prefecture);
    Optional<Area> findByNameInEnglishAndPrefectureNameInEnglish(String nameInEnglish, String prefectureNameInEnglish);
} 