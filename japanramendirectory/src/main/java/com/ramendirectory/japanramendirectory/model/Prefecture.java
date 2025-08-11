package com.ramendirectory.japanramendirectory.model;

import jakarta.persistence.*;


import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "prefectures")
public class Prefecture {
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameInEnglish() {
		return nameInEnglish;
	}

	public void setNameInEnglish(String nameInEnglish) {
		this.nameInEnglish = nameInEnglish;
	}

	public Set<Area> getAreas() {
		return areas;
	}

	public void setAreas(Set<Area> areas) {
		this.areas = areas;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String nameInEnglish;
    
    @OneToMany(mappedBy = "prefecture", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Area> areas;
} 