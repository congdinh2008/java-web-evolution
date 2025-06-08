package com.congdinh.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;

import com.congdinh.dto.CategoryDTO;
import com.congdinh.dto.mapper.CategoryMapper;
import com.congdinh.models.Category;
import com.congdinh.repositories.CategoryJPARepository;

/**
 * Service layer for Category-related operations
 */
@Service
@Transactional
public class CategoryService {
    
    private final CategoryJPARepository categoryRepository;
    
    @Autowired
    public CategoryService(CategoryJPARepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    /**
     * Find all categories
     * @return List of all categories as DTOs
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return CategoryMapper.toDTOList(categories);
    }
    
    /**
     * Find category by ID
     * @param id Category ID
     * @return Optional containing the category DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<CategoryDTO> findCategoryById(Integer id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        return categoryOpt.map(CategoryMapper::toDTO);
    }
    
    /**
     * Save a category (insert or update)
     * @param categoryDTO Category DTO to save
     * @return Saved category DTO with ID
     */
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        Category category = CategoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return CategoryMapper.toDTO(savedCategory);
    }
    
    /**
     * Delete a category by ID
     * @param id Category ID to delete
     */
    public void deleteCategoryById(Integer id) {
        categoryRepository.deleteById(id);
    }
    
    /**
     * Check if a category exists by ID
     * @param id Category ID to check
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean categoryExistsById(Integer id) {
        return categoryRepository.existsById(id);
    }
    
    /**
     * Check if category name exists (for validation during creation)
     * @param name Category name to check
     * @return true if name exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean categoryNameExists(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }
    
    /**
     * Check if category name exists excluding current category (for validation during update)
     * @param name Category name to check
     * @param id Category ID to exclude from check
     * @return true if name exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean categoryNameExistsExcludingId(String name, Integer id) {
        return categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }
    
    /**
     * Get paginated categories
     * @param page Page number (0-based)
     * @param size Page size
     * @param sortBy Property to sort by
     * @param direction Sort direction (ASC or DESC)
     * @return Page of category DTOs
     */
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findPaginatedCategories(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("DESC") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        
        return categoryPage.map(CategoryMapper::toDTO);
    }
    
    /**
     * Search categories by name with pagination
     * @param name Search term for category name
     * @param page Page number (0-based)
     * @param size Page size
     * @param sortBy Property to sort by
     * @param direction Sort direction (ASC or DESC)
     * @return Page of category DTOs matching the search
     */
    @Transactional(readOnly = true)
    public Page<CategoryDTO> searchCategoriesByName(String name, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("DESC") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Category> categoryPage = categoryRepository.findByNameContainingIgnoreCase(name, pageable);
        
        return categoryPage.map(CategoryMapper::toDTO);
    }
    
    /**
     * Count products in a category
     * @param categoryId Category ID
     * @return Number of products in the category
     */
    @Transactional(readOnly = true)
    public long countProductsInCategory(Integer categoryId) {
        return categoryRepository.countProductsInCategory(categoryId);
    }
    
    /**
     * Find categories that have at least one product
     * @return List of categories with products
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> findCategoriesWithProducts() {
        List<Category> categories = categoryRepository.findCategoriesWithProducts();
        return CategoryMapper.toDTOList(categories);
    }
    
    /**
     * Get total number of categories
     * @return Total count of categories
     */
    @Transactional(readOnly = true)
    public long getTotalCategoryCount() {
        return categoryRepository.count();
    }
}
