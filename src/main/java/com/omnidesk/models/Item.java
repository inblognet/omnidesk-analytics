package com.omnidesk.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price;

    // Constructors
    public Item() {}

    public Item(String name, Integer quantity, Double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getQuantity() { return quantity; }
    public Double getPrice() { return price; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}