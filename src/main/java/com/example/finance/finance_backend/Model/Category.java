package com.example.finance.finance_backend.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    public Category() {
    }

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}

/*
 * public enum Category {
 * FOOD("Food"),
 * TRANSPORTATION("Transportation"),
 * ENTERTAINMENT("Entertainment"),
 * HOUSING("Housing"),
 * UTILITIES("Utilities"),
 * HEALTHCARE("Healthcare"),
 * EDUCATION("Education"),
 * PERSONAL_CARE("Personal Care"),
 * SALARY("Salary"),
 * TRAVEL("Travel"),
 * SHOPPING("Shopping"),
 * OTHER("Other");
 * 
 * private final String displayName;
 * 
 * Category(String displayName) {
 * this.displayName = displayName;
 * }
 * 
 * public String getDisplayName() {
 * return displayName;
 * }
 * }
 */