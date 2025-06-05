package com.congdinh.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.congdinh.models.Product;
import com.congdinh.repositories.ProductJPARepository;

/**
 * Service layer for Product-related operations
 */
@Service
@Transactional
public class ProductService {
    
    private final ProductJPARepository productRepository;
    
    @Autowired
    public ProductService(ProductJPARepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * Find all products
     * @return List of all products
     */
    @Transactional(readOnly = true)
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
    
    /**
     * Find product by ID
     * @param id Product ID
     * @return Optional containing the product if found
     */
    @Transactional(readOnly = true)
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
     */
    public void deleteProductById(Integer id) {
        productRepository.deleteById(id);
    }
    
    /**
     * Check if a product exists by ID
     * @param id Product ID to check
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean productExistsById(Integer id) {
        return productRepository.existsById(id);
    }
}
