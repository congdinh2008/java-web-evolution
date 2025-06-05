package com.congdinh.dto.mapper;

import com.congdinh.dto.ProductDTO;
import com.congdinh.models.Product;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between Product entity and ProductDTO
 */
public class ProductMapper {
    
    /**
     * Convert Product entity to ProductDTO
     * @param product Product entity
     * @return ProductDTO
     */
    public static ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getUnitPrice(),
            product.getUnitInStock(),
            product.getThumbnailUrl()
        );
    }
    
    /**
     * Convert ProductDTO to Product entity
     * @param productDTO ProductDTO
     * @return Product entity
     */
    public static Product toEntity(ProductDTO productDTO) {
        if (productDTO == null) {
            return null;
        }
        
        Product product = new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setUnitPrice(productDTO.getUnitPrice());
        product.setUnitInStock(productDTO.getUnitInStock());
        product.setThumbnailUrl(productDTO.getThumbnailUrl());
        
        return product;
    }
    
    /**
     * Convert a list of Product entities to a list of ProductDTOs
     * @param products List of Product entities
     * @return List of ProductDTOs
     */
    public static List<ProductDTO> toDTOList(List<Product> products) {
        return products.stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert a list of ProductDTOs to a list of Product entities
     * @param productDTOs List of ProductDTOs
     * @return List of Product entities
     */
    public static List<Product> toEntityList(List<ProductDTO> productDTOs) {
        return productDTOs.stream()
                .map(ProductMapper::toEntity)
                .collect(Collectors.toList());
    }
}
