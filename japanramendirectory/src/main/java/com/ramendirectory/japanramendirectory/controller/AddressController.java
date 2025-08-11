package com.ramendirectory.japanramendirectory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ramendirectory.japanramendirectory.dto.AreaDTO;
import com.ramendirectory.japanramendirectory.model.Address;
import com.ramendirectory.japanramendirectory.model.Area;
import com.ramendirectory.japanramendirectory.model.Prefecture;
import com.ramendirectory.japanramendirectory.service.AddressService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // Prefecture endpoints
    
    @GetMapping("/prefectures")
    public ResponseEntity<List<Prefecture>> getAllPrefectures() {
        List<Prefecture> prefectures = addressService.getAllPrefectures();
        return new ResponseEntity<>(prefectures, HttpStatus.OK);
    }
    
    @GetMapping("/prefectures/{id}")
    public ResponseEntity<Prefecture> getPrefectureById(@PathVariable Long id) {
        Optional<Prefecture> prefecture = addressService.getPrefectureById(id);
        return prefecture.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PostMapping("/prefectures")
    public ResponseEntity<Prefecture> createPrefecture(@RequestBody Prefecture prefecture) {
        Prefecture newPrefecture = addressService.createPrefecture(prefecture);
        return new ResponseEntity<>(newPrefecture, HttpStatus.CREATED);
    }
    
    @PutMapping("/prefectures/{id}")
    public ResponseEntity<Prefecture> updatePrefecture(@PathVariable Long id, @RequestBody Prefecture prefecture) {
        Optional<Prefecture> updatedPrefecture = addressService.updatePrefecture(id, prefecture);
        return updatedPrefecture.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @DeleteMapping("/prefectures/{id}")
    public ResponseEntity<Void> deletePrefecture(@PathVariable Long id) {
        boolean deleted = addressService.deletePrefecture(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    // Area endpoints
    
    @GetMapping("/areas")
    public ResponseEntity<List<AreaDTO>> getAllAreas() {
        List<Area> areas = addressService.getAllAreas();
        List<AreaDTO> areaDTOs = areas.stream()
            .map(AreaDTO::fromEntity)
            .collect(Collectors.toList());
        return new ResponseEntity<>(areaDTOs, HttpStatus.OK);
    }
    
    @GetMapping("/areas/{id}")
    public ResponseEntity<AreaDTO> getAreaById(@PathVariable Long id) {
        Optional<Area> area = addressService.getAreaById(id);
        return area.map(a -> new ResponseEntity<>(AreaDTO.fromEntity(a), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/prefectures/{prefectureId}/areas")
    public ResponseEntity<List<AreaDTO>> getAreasByPrefecture(@PathVariable Long prefectureId) {
        List<Area> areas = addressService.getAreasByPrefectureId(prefectureId);
        List<AreaDTO> areaDTOs = areas.stream()
            .map(AreaDTO::fromEntity)
            .collect(Collectors.toList());
        return new ResponseEntity<>(areaDTOs, HttpStatus.OK);
    }
    
    @PostMapping("/areas")
    public ResponseEntity<Area> createArea(@RequestBody Area area) {
        Area newArea = addressService.createArea(area);
        return new ResponseEntity<>(newArea, HttpStatus.CREATED);
    }
    
    @PutMapping("/areas/{id}")
    public ResponseEntity<Area> updateArea(@PathVariable Long id, @RequestBody Area area) {
        Optional<Area> updatedArea = addressService.updateArea(id, area);
        return updatedArea.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @DeleteMapping("/areas/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable Long id) {
        boolean deleted = addressService.deleteArea(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    // Address endpoints
    
    @GetMapping
    public ResponseEntity<List<Address>> getAllAddresses() {
        List<Address> addresses = addressService.getAllAddresses();
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable Long id) {
        Optional<Address> address = addressService.getAddressById(id);
        return address.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/areas/{areaId}/addresses")
    public ResponseEntity<List<Address>> getAddressesByArea(@PathVariable Long areaId) {
        List<Address> addresses = addressService.getAddressesByAreaId(areaId);
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }
    
    @GetMapping("/prefectures/{prefectureId}/addresses")
    public ResponseEntity<List<Address>> getAddressesByPrefecture(@PathVariable Long prefectureId) {
        List<Address> addresses = addressService.getAddressesByPrefectureId(prefectureId);
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        Address newAddress = addressService.createAddress(address);
        return new ResponseEntity<>(newAddress, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long id, @RequestBody Address address) {
        Optional<Address> updatedAddress = addressService.updateAddress(id, address);
        return updatedAddress.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        boolean deleted = addressService.deleteAddress(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
} 