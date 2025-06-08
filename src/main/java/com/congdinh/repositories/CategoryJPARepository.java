package com.congdinh.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.congdinh.models.Category;

/**
 * Spring Data JPA repository for Category entity
 * Provides all the standard CRUD operations from JpaRepository plus custom queries
 */
@Repository
public interface CategoryJPARepository extends JpaRepository<Category, Integer> {
    
    /**
     * Find category by name (case insensitive)
     * @param name Category name to search for
     * @return Optional containing the category if found
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(?1)")
    Optional<Category> findByNameIgnoreCase(String name);
    
    /**
     * Find categories by name containing the search term (case insensitive)
     * @param name Search term for category name
     * @param pageable Pagination information
     * @return Page of categories matching the search
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Category> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    /**
     * Find categories that have at least one product
     * @return List of categories with products
     */
    @Query("SELECT DISTINCT c FROM Category c WHERE c.products IS NOT EMPTY")
    List<Category> findCategoriesWithProducts();
    
    /**
     * Count products in a category
     * @param categoryId Category ID
     * @return Number of products in the category
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countProductsInCategory(@Param("categoryId") Integer categoryId);
    
    /**
     * Check if category name exists (for validation, excluding current category)
     * @param name Category name to check
     * @param id Category ID to exclude from check
     * @return true if name exists, false otherwise
     */
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE LOWER(c.name) = LOWER(:name) AND c.id != :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Integer id);
    
    /**
     * Check if category name exists (for validation during creation)
     * @param name Category name to check
     * @return true if name exists, false otherwise
     */
    boolean existsByNameIgnoreCase(String name);
}
