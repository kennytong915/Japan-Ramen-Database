package com.ramendirectory.japanramendirectory.service;

import java.util.List;
import java.util.Optional;

import com.ramendirectory.japanramendirectory.model.Address;
import com.ramendirectory.japanramendirectory.model.Area;
import com.ramendirectory.japanramendirectory.model.Prefecture;

public interface AddressService {
    // Prefecture methods
    List<Prefecture> getAllPrefectures();
    Optional<Prefecture> getPrefectureById(Long id);
    Optional<Prefecture> getPrefectureByName(String name);
    Prefecture createPrefecture(Prefecture prefecture);
    Optional<Prefecture> updatePrefecture(Long id, Prefecture prefecture);
    boolean deletePrefecture(Long id);
    
    // Area methods
    List<Area> getAllAreas();
    List<Area> getAreasByPrefecture(Prefecture prefecture);
    List<Area> getAreasByPrefectureId(Long prefectureId);
    Optional<Area> getAreaById(Long id);
    Optional<Area> getAreaByNameAndPrefecture(String name, Prefecture prefecture);
    Area createArea(Area area);
    Optional<Area> updateArea(Long id, Area area);
    boolean deleteArea(Long id);
    
    // Address methods
    List<Address> getAllAddresses();
    List<Address> getAddressesByArea(Area area);
    List<Address> getAddressesByAreaId(Long areaId);
    List<Address> getAddressesByPrefectureId(Long prefectureId);
    Optional<Address> getAddressById(Long id);
    Address createAddress(Address address);
    Optional<Address> updateAddress(Long id, Address address);
    boolean deleteAddress(Long id);
} 