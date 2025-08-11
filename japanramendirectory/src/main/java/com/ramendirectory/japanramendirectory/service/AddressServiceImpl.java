package com.ramendirectory.japanramendirectory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ramendirectory.japanramendirectory.model.Address;
import com.ramendirectory.japanramendirectory.model.Area;
import com.ramendirectory.japanramendirectory.model.Prefecture;
import com.ramendirectory.japanramendirectory.repository.AddressRepository;
import com.ramendirectory.japanramendirectory.repository.AreaRepository;
import com.ramendirectory.japanramendirectory.repository.PrefectureRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {
    
    private final PrefectureRepository prefectureRepository;
    private final AreaRepository areaRepository;
    private final AddressRepository addressRepository;
    
    @Autowired
    public AddressServiceImpl(
            PrefectureRepository prefectureRepository,
            AreaRepository areaRepository,
            AddressRepository addressRepository) {
        this.prefectureRepository = prefectureRepository;
        this.areaRepository = areaRepository;
        this.addressRepository = addressRepository;
    }
    
    // Prefecture methods
    
    @Override
    public List<Prefecture> getAllPrefectures() {
        return prefectureRepository.findAll();
    }
    
    @Override
    public Optional<Prefecture> getPrefectureById(Long id) {
        return prefectureRepository.findById(id);
    }
    
    @Override
    public Optional<Prefecture> getPrefectureByName(String name) {
        return prefectureRepository.findByName(name);
    }
    
    @Override
    public Prefecture createPrefecture(Prefecture prefecture) {
        return prefectureRepository.save(prefecture);
    }
    
    @Override
    public Optional<Prefecture> updatePrefecture(Long id, Prefecture prefecture) {
        Optional<Prefecture> existingPrefecture = prefectureRepository.findById(id);
        if (existingPrefecture.isPresent()) {
            Prefecture prefectureToUpdate = existingPrefecture.get();
            prefectureToUpdate.setName(prefecture.getName());
            prefectureToUpdate.setNameInEnglish(prefecture.getNameInEnglish());
            return Optional.of(prefectureRepository.save(prefectureToUpdate));
        }
        return Optional.empty();
    }
    
    @Override
    public boolean deletePrefecture(Long id) {
        if (prefectureRepository.existsById(id)) {
            prefectureRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Area methods
    
    @Override
    public List<Area> getAllAreas() {
        return areaRepository.findAll();
    }
    
    @Override
    public List<Area> getAreasByPrefecture(Prefecture prefecture) {
        return areaRepository.findByPrefecture(prefecture);
    }
    
    @Override
    public List<Area> getAreasByPrefectureId(Long prefectureId) {
        return areaRepository.findByPrefectureId(prefectureId);
    }
    
    @Override
    public Optional<Area> getAreaById(Long id) {
        return areaRepository.findById(id);
    }
    
    @Override
    public Optional<Area> getAreaByNameAndPrefecture(String name, Prefecture prefecture) {
        return areaRepository.findByNameAndPrefecture(name, prefecture);
    }
    
    @Override
    public Area createArea(Area area) {
        return areaRepository.save(area);
    }
    
    @Override
    public Optional<Area> updateArea(Long id, Area area) {
        Optional<Area> existingArea = areaRepository.findById(id);
        if (existingArea.isPresent()) {
            Area areaToUpdate = existingArea.get();
            areaToUpdate.setName(area.getName());
            areaToUpdate.setNameInEnglish(area.getNameInEnglish());
            areaToUpdate.setPrefecture(area.getPrefecture());
            return Optional.of(areaRepository.save(areaToUpdate));
        }
        return Optional.empty();
    }
    
    @Override
    public boolean deleteArea(Long id) {
        if (areaRepository.existsById(id)) {
            areaRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Address methods
    
    @Override
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }
    
    @Override
    public List<Address> getAddressesByArea(Area area) {
        return addressRepository.findByArea(area);
    }
    
    @Override
    public List<Address> getAddressesByAreaId(Long areaId) {
        return addressRepository.findByAreaId(areaId);
    }
    
    @Override
    public List<Address> getAddressesByPrefectureId(Long prefectureId) {
        return addressRepository.findByAreaPrefectureId(prefectureId);
    }
    
    @Override
    public Optional<Address> getAddressById(Long id) {
        return addressRepository.findById(id);
    }
    
    @Override
    public Address createAddress(Address address) {
        return addressRepository.save(address);
    }
    
    @Override
    public Optional<Address> updateAddress(Long id, Address address) {
        Optional<Address> existingAddress = addressRepository.findById(id);
        if (existingAddress.isPresent()) {
            Address addressToUpdate = existingAddress.get();
            addressToUpdate.setDetailedAddress(address.getDetailedAddress());
            addressToUpdate.setArea(address.getArea());
            addressToUpdate.setBuilding(address.getBuilding());
            addressToUpdate.setFloor(address.getFloor());
            addressToUpdate.setUnit(address.getUnit());
            addressToUpdate.setPostalCode(address.getPostalCode());
            return Optional.of(addressRepository.save(addressToUpdate));
        }
        return Optional.empty();
    }
    
    @Override
    public boolean deleteAddress(Long id) {
        if (addressRepository.existsById(id)) {
            addressRepository.deleteById(id);
            return true;
        }
        return false;
    }
} 
