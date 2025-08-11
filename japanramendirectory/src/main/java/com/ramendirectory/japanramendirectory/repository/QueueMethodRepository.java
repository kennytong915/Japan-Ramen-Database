package com.ramendirectory.japanramendirectory.repository;

import com.ramendirectory.japanramendirectory.model.QueueMethod;
import com.ramendirectory.japanramendirectory.model.QueueType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QueueMethodRepository extends JpaRepository<QueueMethod, Long> {
    Optional<QueueMethod> findByType(QueueType type);
} 