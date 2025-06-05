package com.congdinh.repositories;

import com.congdinh.models.Product;

/**
 * Extended repository interface for Products with additional methods
 */
public interface ProductRepositoryInterface extends Repository<Product, Integer> {
    
    /**
     * Create Products table if it doesn't exist
     */
    void createProductsTableIfNotExists();
    
    /**
     * Initialize sample data if the table is empty
     */
    void initializeSampleData();
}
