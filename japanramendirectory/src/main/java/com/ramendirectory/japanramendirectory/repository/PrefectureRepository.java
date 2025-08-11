package com.ramendirectory.japanramendirectory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ramendirectory.japanramendirectory.model.Prefecture;

import java.util.Optional;

@Repository
public interface PrefectureRepository extends JpaRepository<Prefecture, Long> {
    Optional<Prefecture> findByName(String name);
    Optional<Prefecture> findByNameInEnglish(String nameInEnglish);
} 