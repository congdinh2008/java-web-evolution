package com.congdinh.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.congdinh.models.Product;

/**
 * Spring Data JPA repository for Product entity
 * Provides all the standard CRUD operations from JpaRepository plus custom queries
 */
@Repository
public interface ProductJPARepository extends JpaRepository<Product, Integer> {
    // Spring Data JPA provides all basic CRUD operations automatically:
    // - findAll(), findById(), save(), deleteById(), existsById(), etc.
    
    // Custom query methods
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByUnitPriceLessThan(double price);
    
    List<Product> findByUnitPriceGreaterThan(double price);
    
    List<Product> findByUnitInStockLessThan(int quantity);
    
    Optional<Product> findByName(String name);
    
    // Pagination and sorting are handled by Spring Data JPA
    Page<Product> findAll(Pageable pageable);
    
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    // JPQL query
    @Query("SELECT p FROM Product p WHERE p.unitPrice BETWEEN :minPrice AND :maxPrice")
    List<Product> findProductsInPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);
    
    // Native SQL query example
    @Query(value = "SELECT * FROM Products WHERE UnitInStock > 0 ORDER BY UnitPrice ASC LIMIT :limit", nativeQuery = true)
    List<Product> findInStockProductsOrderByPriceAsc(@Param("limit") int limit);
}
