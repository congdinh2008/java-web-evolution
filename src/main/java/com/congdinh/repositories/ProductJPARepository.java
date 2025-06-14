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
import com.congdinh.models.Category;

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
    
    // Category-based queries
    List<Product> findByCategory(Category category);
    
    Page<Product> findByCategory(Category category, Pageable pageable);
    
    List<Product> findByCategoryId(Integer categoryId);
    
    Page<Product> findByCategoryId(Integer categoryId, Pageable pageable);
    
    // Combined search queries
    List<Product> findByNameContainingIgnoreCaseAndCategory(String name, Category category);
    
    Page<Product> findByNameContainingIgnoreCaseAndCategory(String name, Category category, Pageable pageable);
    
    // Pagination and sorting are handled by Spring Data JPA
    Page<Product> findAll(Pageable pageable);
    
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    // JPQL query
    @Query("SELECT p FROM Product p WHERE p.unitPrice BETWEEN :minPrice AND :maxPrice")
    List<Product> findProductsInPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);
    
    // Native SQL query example
    @Query(value = "SELECT * FROM Products WHERE UnitInStock > 0 ORDER BY UnitPrice ASC LIMIT :limit", nativeQuery = true)
    List<Product> findInStockProductsOrderByPriceAsc(@Param("limit") int limit);
    
    // Category-related counts
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category")
    long countByCategory(@Param("category") Category category);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Integer categoryId);
    
    // Statistics methods for dashboard
    long countByUnitInStockGreaterThan(int stock);
    
    long countByUnitInStockLessThanEqual(int stock);
    
    long countByUnitInStock(int stock);
    
    List<Product> findByUnitInStockLessThanEqual(int stock);
}
