package com.congdinh.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.congdinh.models.Product;

/**
 * Spring Data JPA repository for Product entity
 * Provides all the standard CRUD operations from JpaRepository plus any custom queries we need
 */
@Repository
public interface ProductJPARepository extends JpaRepository<Product, Integer> {
    // Spring Data JPA provides all basic CRUD operations automatically:
    // - findAll(), findById(), save(), deleteById(), existsById(), etc.
    
    // Custom query methods can be added here if needed, for example:
    // List<Product> findByNameContaining(String name);
    // List<Product> findByUnitPriceLessThan(double price);
    // Optional<Product> findByName(String name);
}
