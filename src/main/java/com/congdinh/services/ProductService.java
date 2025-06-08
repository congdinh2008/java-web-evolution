package com.congdinh.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;

import com.congdinh.dto.ProductDTO;
import com.congdinh.dto.mapper.ProductMapper;
import com.congdinh.models.Product;
import com.congdinh.models.Category;
import com.congdinh.repositories.ProductJPARepository;
import com.congdinh.repositories.CategoryJPARepository;

/**
 * Service layer for Product-related operations
 */
@Service
@Transactional
public class ProductService {
    
    private final ProductJPARepository productRepository;
    private final CategoryJPARepository categoryRepository;
    
    public ProductService(ProductJPARepository productRepository, CategoryJPARepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    
    /**
     * Find all products
     * @return List of all products as DTOs
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findAllProducts() {
        List<Product> products = productRepository.findAll();
        return ProductMapper.toDTOList(products);
    }
    
    /**
     * Find product by ID
     * @param id Product ID
     * @return Optional containing the product DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findProductById(Integer id) {
        Optional<Product> productOpt = productRepository.findById(id);
        return productOpt.map(ProductMapper::toDTO);
    }
    
    /**
     * Save a product (insert or update) with proper category handling
     * @param productDTO Product DTO to save
     * @return Saved product DTO with ID
     */
    public ProductDTO saveProduct(ProductDTO productDTO) {
        Product product = ProductMapper.toEntity(productDTO);
        
        // Handle category assignment
        if (productDTO.getCategoryId() != null) {
            Optional<Category> categoryOpt = categoryRepository.findById(productDTO.getCategoryId());
            if (categoryOpt.isPresent()) {
                product.setCategory(categoryOpt.get());
            }
        }
        
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toDTO(savedProduct);
    }

    /**
     * Save a product with category ID
     * @param productDTO Product DTO to save
     * @param categoryId Category ID to assign
     * @return Saved product DTO with ID
     */
    public ProductDTO saveProductWithCategory(ProductDTO productDTO, Integer categoryId) {
        Product product = ProductMapper.toEntity(productDTO);
        
        if (categoryId != null) {
            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
            if (categoryOpt.isPresent()) {
                product.setCategory(categoryOpt.get());
            }
        }
        
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toDTO(savedProduct);
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
    
    /**
     * Find products by name (case insensitive)
     * @param name Name to search for
     * @return List of product DTOs matching the name
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findProductsByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return ProductMapper.toDTOList(products);
    }
    
    /**
     * Find products with a price less than the specified value
     * @param price Maximum price
     * @return List of product DTOs with price less than specified
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findProductsByPriceLessThan(double price) {
        List<Product> products = productRepository.findByUnitPriceLessThan(price);
        return ProductMapper.toDTOList(products);
    }
    
    /**
     * Find products with a price greater than the specified value
     * @param price Minimum price
     * @return List of product DTOs with price greater than specified
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findProductsByPriceGreaterThan(double price) {
        List<Product> products = productRepository.findByUnitPriceGreaterThan(price);
        return ProductMapper.toDTOList(products);
    }
    
    /**
     * Find products with low stock (less than specified quantity)
     * @param quantity Quantity threshold
     * @return List of product DTOs with stock less than specified
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findProductsWithLowStock(int quantity) {
        List<Product> products = productRepository.findByUnitInStockLessThan(quantity);
        return ProductMapper.toDTOList(products);
    }
    
    /**
     * Find a product by exact name
     * @param name Product name
     * @return Optional containing the product DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findProductByExactName(String name) {
        Optional<Product> productOpt = productRepository.findByName(name);
        return productOpt.map(ProductMapper::toDTO);
    }
    
    /**
     * Find products within a price range
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of product DTOs within the price range
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findProductsInPriceRange(double minPrice, double maxPrice) {
        List<Product> products = productRepository.findProductsInPriceRange(minPrice, maxPrice);
        return ProductMapper.toDTOList(products);
    }
    
    /**
     * Find in-stock products ordered by price (ascending)
     * @param limit Maximum number of products to return
     * @return List of product DTOs in stock, ordered by price
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findInStockProductsOrderByPriceAsc(int limit) {
        List<Product> products = productRepository.findInStockProductsOrderByPriceAsc(limit);
        return ProductMapper.toDTOList(products);
    }
    
    /**
     * Get paginated products
     * @param page Page number (0-based)
     * @param size Page size
     * @param sortBy Property to sort by
     * @param direction Sort direction (ASC or DESC)
     * @return Page of product DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> findPaginatedProducts(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("DESC") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage = productRepository.findAll(pageable);
        
        return productPage.map(ProductMapper::toDTO);
    }
    
    /**
     * Get paginated products filtered by name
     * @param name Name to filter by
     * @param page Page number (0-based)
     * @param size Page size
     * @return Page of product DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> findPaginatedProductsByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByNameContainingIgnoreCase(name, pageable);
        
        return productPage.map(ProductMapper::toDTO);
    }
    
    /**
     * Search products by name with price range and pagination
     * @param name Name to search for (optional)
     * @param minPrice Minimum price (optional)
     * @param maxPrice Maximum price (optional)
     * @param page Page number (0-based)
     * @param size Page size
     * @param sortBy Property to sort by
     * @param direction Sort direction (ASC or DESC)
     * @return Page of product DTOs matching the search criteria
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProductsByName(String name, Double minPrice, Double maxPrice, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("DESC") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage;
        
        if (name != null && !name.trim().isEmpty()) {
            productPage = productRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }
        
        return productPage.map(ProductMapper::toDTO);
    }

    /**
     * Find products by category
     * @param categoryId Category ID
     * @return List of product DTOs in the category
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findProductsByCategory(Integer categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isPresent()) {
            List<Product> products = productRepository.findByCategory(categoryOpt.get());
            return ProductMapper.toDTOList(products);
        }
        return List.of();
    }

    /**
     * Find products by category with pagination
     * @param categoryId Category ID
     * @param page Page number (0-based)
     * @param size Page size
     * @param sortBy Property to sort by
     * @param direction Sort direction (ASC or DESC)
     * @return Page of product DTOs in the category
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> findProductsByCategoryPaginated(Integer categoryId, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("DESC") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isPresent()) {
            Page<Product> productPage = productRepository.findByCategory(categoryOpt.get(), pageable);
            return productPage.map(ProductMapper::toDTO);
        }
        
        // Return empty page if category not found
        return Page.empty(pageable);
    }
}
