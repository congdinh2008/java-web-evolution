package com.congdinh.dto;

/**
 * Data Transfer Object for Product
 * Used to transfer product data between layers without exposing domain model details
 */
public class ProductDTO {
    private int id;
    private String name;
    private double unitPrice;
    private int unitInStock;
    private String thumbnailUrl;

    // Constructors
    public ProductDTO() {
    }

    public ProductDTO(int id, String name, double unitPrice, int unitInStock, String thumbnailUrl) {
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
        return "ProductDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unitPrice=" + unitPrice +
                ", unitInStock=" + unitInStock +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                '}';
    }
}
