package com.congdinh.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Product entity class with JPA annotations
 */
@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "UnitPrice", nullable = false)
    private double unitPrice;
    
    @Column(name = "UnitInStock", nullable = false)
    private int unitInStock;
    
    @Column(name = "ThumbnailUrl", nullable = false)
    private String thumbnailUrl;

    // Constructors
    public Product() {
    }

    public Product(int id, String name, double unitPrice, int unitInStock, String thumbnailUrl) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.unitInStock = unitInStock;
        this.thumbnailUrl = thumbnailUrl;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getUnitInStock() {
        return unitInStock;
    }

    public void setUnitInStock(int unitInStock) {
        this.unitInStock = unitInStock;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unitPrice=" + unitPrice +
                ", unitInStock=" + unitInStock +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                '}';
    }
}
