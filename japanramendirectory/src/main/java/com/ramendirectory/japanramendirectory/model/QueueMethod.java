package com.ramendirectory.japanramendirectory.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "queue_methods")
public class QueueMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QueueType type;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String detailedGuide;
    
    @OneToOne(mappedBy = "queueMethod")
    private Restaurant restaurant;
    
    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QueueType getType() {
        return type;
    }

    public void setType(QueueType type) {
        this.type = type;
    }

    public String getDetailedGuide() {
        return detailedGuide;
    }

    public void setDetailedGuide(String detailedGuide) {
        this.detailedGuide = detailedGuide;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
} 