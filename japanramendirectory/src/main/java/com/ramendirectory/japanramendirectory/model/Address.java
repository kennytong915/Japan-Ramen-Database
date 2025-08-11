package com.ramendirectory.japanramendirectory.model;

import jakarta.persistence.*;

@Entity
public class Address {
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getDetailedAddress() {
		return detailedAddress;
	}

	public void setDetailedAddress(String detailedAddress) {
		this.detailedAddress = detailedAddress;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;
    
    @Column(nullable = false)
    private String detailedAddress;
    
    // Optional additional fields
    private String building;
    private String floor;
    private String unit;
    private String postalCode;
    
    // Getters for full address representations
    
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(area.getPrefecture().getName())
          .append(area.getName())
          .append(detailedAddress);
        
        if (building != null && !building.isEmpty()) {
            sb.append(" ").append(building);
            
            if (floor != null && !floor.isEmpty()) {
                sb.append(" ").append(floor);
            }
            
            if (unit != null && !unit.isEmpty()) {
                sb.append(" ").append(unit);
            }
        }
        
        return sb.toString();
    }
    
    public String getFullAddressInEnglish() {
        // Format for English is typically reverse of Japanese
        StringBuilder sb = new StringBuilder();
        
        if (building != null && !building.isEmpty()) {
            sb.append(building);
            
            if (floor != null && !floor.isEmpty()) {
                sb.append(", ").append(floor);
            }
            
            if (unit != null && !unit.isEmpty()) {
                sb.append(", ").append(unit);
            }
            
            sb.append(", ");
        }
        
        sb.append(detailedAddress)
          .append(", ")
          .append(area.getNameInEnglish())
          .append(", ")
          .append(area.getPrefecture().getNameInEnglish());
        
        if (postalCode != null && !postalCode.isEmpty()) {
            sb.append(" ").append(postalCode);
        }
        
        return sb.toString();
    }
} 