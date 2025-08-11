package com.ramendirectory.japanramendirectory.model;

import jakarta.persistence.*;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Date;

@Entity
@Table(name = "restaurants")
public class Restaurant {
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

	public Set<Genre> getGenres() {
		return genres;
	}

	public void setGenres(Set<Genre> genres) {
		this.genres = genres;
	}

	public Set<SoupBase> getSoupBases() {
		return soupBases;
	}

	public void setSoupBases(Set<SoupBase> soupBases) {
		this.soupBases = soupBases;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public Boolean getReservationSystem() {
		return reservationSystem;
	}

	public void setReservationSystem(Boolean reservationSystem) {
		this.reservationSystem = reservationSystem;
	}

	public QueueMethod getQueueMethod() {
		return queueMethod;
	}

	public void setQueueMethod(QueueMethod queueMethod) {
		this.queueMethod = queueMethod;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Integer getSeats() {
		return seats;
	}

	public void setSeats(Integer seats) {
		this.seats = seats;
	}

	public Map<String, String> getSocialMediaLinks() {
		return socialMediaLinks;
	}

	public void setSocialMediaLinks(Map<String, String> socialMediaLinks) {
		this.socialMediaLinks = socialMediaLinks;
	}
	
	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	
	public String getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(String openingHours) {
		this.openingHours = openingHours;
	}
	
	public String getRestDay() {
		return restDay;
	}

	public void setRestDay(String restDay) {
		this.restDay = restDay;
	}

	public Date getOpeningDate() {
		return openingDate;
	}

	public void setOpeningDate(Date openingDate) {
		this.openingDate = openingDate;
	}

	public RestaurantDescription getDescription() {
		return description;
	}

	public void setDescription(RestaurantDescription description) {
		this.description = description;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "restaurant_genres",
            joinColumns = @JoinColumn(name = "restaurant_id"))
    @Column(name = "genre")
    private Set<Genre> genres = new HashSet<>();

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "restaurant_soup_bases",
            joinColumns = @JoinColumn(name = "restaurant_id"))
    @Column(name = "soup_base")
    private Set<SoupBase> soupBases = new HashSet<>();

    private Double score;
    private Boolean reservationSystem;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "queue_method_id")
    private QueueMethod queueMethod;
    
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;
    
    private Integer seats;

    @ElementCollection
    @CollectionTable(name = "restaurant_social_media_links",
            joinColumns = @JoinColumn(name = "restaurant_id"))
    @MapKeyColumn(name = "platform")
    @Column(name = "url")
    private Map<String, String> socialMediaLinks;
    
    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Menu menu;

    @Column(length = 1000)
    private String openingHours;
    
    @Column(length = 200)
    private String restDay;
    
    @Temporal(TemporalType.DATE)
    private Date openingDate;
    
    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RestaurantDescription description;
}