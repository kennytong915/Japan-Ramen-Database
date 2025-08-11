package com.ramendirectory.japanramendirectory.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "areas")
public class Area {
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

	public Prefecture getPrefecture() {
		return prefecture;
	}

	public void setPrefecture(Prefecture prefecture) {
		this.prefecture = prefecture;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	private String nameInEnglish;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "prefecture_id", nullable = false)
	@JsonBackReference
	private Prefecture prefecture;

	// Business logic methods

	public String getFullName() {
		return prefecture.getName() + " " + name;
	}

	public String getFullNameInEnglish() {
		return nameInEnglish + ", " + prefecture.getNameInEnglish();
	}
}