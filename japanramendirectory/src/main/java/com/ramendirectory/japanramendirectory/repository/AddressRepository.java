package com.ramendirectory.japanramendirectory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ramendirectory.japanramendirectory.model.Address;
import com.ramendirectory.japanramendirectory.model.Area;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByArea(Area area);
    List<Address> findByAreaId(Long areaId);
    List<Address> findByAreaPrefectureId(Long prefectureId);
    List<Address> findByPostalCode(String postalCode);
} 