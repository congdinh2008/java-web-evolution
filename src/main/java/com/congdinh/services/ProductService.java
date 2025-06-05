package com.congdinh.services;

import java.util.List;
import java.util.Optional;

import com.congdinh.models.Product;
import com.congdinh.repositories.ProductRepository;

/**
 * Service layer for Product-related operations
 */
public class ProductService {
    
    private ProductRepository productRepository;
    
    // Default constructor for non-Spring contexts
    public ProductService() {
    }
    
    // Constructor with dependency injection
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    // Setter for dependency injection by Spring
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * Find all products
     * @return List of all products
     */
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
    
    /**
     * Find product by ID
     * @param id Product ID
     * @return Optional containing the product if found
     */
    public Optional<Product> findProductById(Integer id) {
        return productRepository.findById(id);
    }
    
    /**
     * Save a product (insert or update)
     * @param product Product to save
     * @return Saved product with ID
     */
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    /**
     * Delete a product by ID
     * @param id Product ID to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteProductById(Integer id) {
        return productRepository.deleteById(id);
    }
    
    /**
     * Check if a product exists by ID
     * @param id Product ID to check
     * @return true if exists, false otherwise
     */
    public boolean productExistsById(Integer id) {
        return productRepository.existsById(id);
    }
    
    /**
     * Initialize the Products table if it doesn't exist
     */
    public void createProductsTableIfNotExists() {
        productRepository.createProductsTableIfNotExists();
    }
    
    /**
     * Initialize sample data if the table is empty
     */
    public void initializeSampleData() {
        productRepository.initializeSampleData();
    }
}
