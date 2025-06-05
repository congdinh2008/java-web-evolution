package com.congdinh.utils;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.congdinh.models.Product;
import com.congdinh.repositories.ProductJPARepository;

/**
 * Component to seed initial data into the database
 * Uses event listener to run after application startup
 */
@Component
public class SeedData {
    
    private final ProductJPARepository productRepository;
    
    @Autowired
    public SeedData(ProductJPARepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * Initialize sample data when the application context is fully loaded
     */
    @EventListener
    @Transactional
    public void seed(ContextRefreshedEvent event) {
        initializeSampleData();
    }
    
    /**
     * Initialize sample data if the table is empty
     */
    private void initializeSampleData() {
        // Check if we have any products
        long count = productRepository.count();
        
        if (count == 0) {
            System.out.println("[SeedData] initializeSampleData: Adding sample data");
            
            // Add some sample products
            List<Product> sampleProducts = Arrays.asList(
                new Product(0, "Smartphone X", 799.99, 50, "https://placehold.co/600x400?text=Smartphone+X"),
                new Product(0, "Laptop Pro", 1299.99, 25, "https://placehold.co/600x400?text=Laptop+Pro"),
                new Product(0, "Wireless Earbuds", 129.99, 100, "https://placehold.co/600x400?text=Wireless+Earbuds"),
                new Product(0, "Smart Watch", 249.99, 30, "https://placehold.co/600x400?text=Smart+Watch"),
                new Product(0, "4K TV", 899.99, 15, "https://placehold.co/600x400?text=4K+TV")
            );
            
            // Insert all products at once
            productRepository.saveAll(sampleProducts);
            
            System.out.println("[SeedData] initializeSampleData: Added " + sampleProducts.size() + " sample products");
        } else {
            System.out.println("[SeedData] initializeSampleData: Table already has data, skipping");
        }
    }
}
