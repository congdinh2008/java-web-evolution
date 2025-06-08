package com.congdinh.dto.mapper;

import com.congdinh.dto.CategoryDTO;
import com.congdinh.models.Category;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between Category entity and CategoryDTO
 */
public class CategoryMapper {
    
    private CategoryMapper() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Convert Category entity to CategoryDTO
     * @param category Category entity
     * @return CategoryDTO
     */
    public static CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }
        
        return new CategoryDTO(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getProducts() != null ? category.getProducts().size() : 0
        );
    }
    
    /**
     * Convert CategoryDTO to Category entity
     * @param categoryDTO CategoryDTO
     * @return Category entity
     */
    public static Category toEntity(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            return null;
        }
        
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        
        return category;
    }
    
    /**
     * Convert a list of Category entities to a list of CategoryDTOs
     * @param categories List of Category entities
     * @return List of CategoryDTOs
     */
    public static List<CategoryDTO> toDTOList(List<Category> categories) {
        return categories.stream()
                .map(CategoryMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert a list of CategoryDTOs to a list of Category entities
     * @param categoryDTOs List of CategoryDTOs
     * @return List of Category entities
     */
    public static List<Category> toEntityList(List<CategoryDTO> categoryDTOs) {
        return categoryDTOs.stream()
                .map(CategoryMapper::toEntity)
                .collect(Collectors.toList());
    }
}
