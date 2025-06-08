package com.congdinh.controllers.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.congdinh.dto.ProductDTO;
import com.congdinh.dto.CategoryDTO;
import com.congdinh.services.ProductService;
import com.congdinh.services.CategoryService;

/**
 * Admin controller for handling product-related operations
 */
@Controller
@RequestMapping("/admin/products")
public class AdminProductController {
    
    // Constants for model attributes and view names
    private static final String TITLE_ATTR = "title";
    private static final String PRODUCT_ATTR = "product";
    private static final String CATEGORIES_ATTR = "categories";
    private static final String IS_EDIT_ATTR = "isEdit";
    private static final String FORM_VIEW = "admin/product/form";
    private static final String REDIRECT_PRODUCTS = "redirect:/admin/products";
    private static final String SUCCESS_MESSAGE_ATTR = "successMessage";
    private static final String ERROR_MESSAGE_ATTR = "errorMessage";
    
    private final ProductService productService;
    private final CategoryService categoryService;
    
    public AdminProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }
    
    /**
     * List all products with search and pagination
     */
    @GetMapping
    public String listProducts(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            Model model) {
        
        try {
            Page<ProductDTO> productPage;
            
            if (search.trim().isEmpty() && category.trim().isEmpty() && status.trim().isEmpty()) {
                productPage = productService.findPaginatedProducts(page, size, sortBy, direction);
            } else {
                productPage = productService.searchProductsByName(search, category, status, page, size, sortBy, direction);
            }
            
            // Calculate stats for dashboard cards using new service methods
            long totalProducts = productService.getTotalProductCount();
            long activeProducts = productService.getActiveProductCount();
            long lowStockProducts = productService.getLowStockProductCount();
            long outOfStockProducts = productService.getOutOfStockProductCount();
            
            // Get categories for filter dropdown
            List<CategoryDTO> categories = categoryService.findAllCategories();
            
            model.addAttribute("products", productPage.getContent());
            model.addAttribute(CATEGORIES_ATTR, categories);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("totalElements", productPage.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("direction", direction);
            model.addAttribute("search", search);
            model.addAttribute("category", category);
            model.addAttribute("status", status);
            
            // Stats for dashboard cards
            model.addAttribute("totalProducts", totalProducts);
            model.addAttribute("activeProducts", activeProducts);
            model.addAttribute("lowStockProducts", lowStockProducts);
            model.addAttribute("outOfStockProducts", outOfStockProducts);
            
            model.addAttribute(TITLE_ATTR, "Admin - Product Management");
            
            return "admin/product/list";
            
        } catch (Exception e) {
            System.err.println("[AdminProductController] listProducts: Error getting products");
            e.printStackTrace(System.err);
            
            model.addAttribute(ERROR_MESSAGE_ATTR, "Error loading products. Please try again.");
            model.addAttribute("products", List.of());
            model.addAttribute(CATEGORIES_ATTR, List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalElements", 0);
            model.addAttribute("totalProducts", 0);
            model.addAttribute("activeProducts", 0);
            model.addAttribute("lowStockProducts", 0);
            model.addAttribute("outOfStockProducts", 0);
            model.addAttribute(TITLE_ATTR, "Admin - Product Management");
            
            return "admin/product/list";
        }
    }
    
    /**
     * Show form to add a new product
     */
    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute(TITLE_ATTR, "Admin - Add New Product");
        model.addAttribute(PRODUCT_ATTR, new ProductDTO());
        model.addAttribute(CATEGORIES_ATTR, categoryService.findAllCategories());
        model.addAttribute(IS_EDIT_ATTR, false);
        return FORM_VIEW;
    }
    
    /**
     * Process form submission to add a new product
     */
    @PostMapping("/add")
    public String addProduct(@ModelAttribute ProductDTO product, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Validate required fields
            if (product.getName() == null || product.getName().trim().isEmpty() || 
                product.getThumbnailUrl() == null || product.getThumbnailUrl().trim().isEmpty()) {
                
                model.addAttribute(ERROR_MESSAGE_ATTR, "Product name and thumbnail URL are required");
                model.addAttribute(PRODUCT_ATTR, product);
                model.addAttribute(CATEGORIES_ATTR, categoryService.findAllCategories());
                model.addAttribute(IS_EDIT_ATTR, false);
                return FORM_VIEW;
            }
            
            // Set category if categoryId is provided
            if (product.getCategoryId() != null && product.getCategoryId() > 0) {
                Optional<CategoryDTO> categoryOpt = categoryService.findCategoryById(product.getCategoryId());
                if (categoryOpt.isPresent()) {
                    product.setCategory(categoryOpt.get());
                }
            }
            
            // Save new product
            ProductDTO savedProduct = productService.saveProduct(product);
            System.out.println("[AdminProductController] addProduct: Product added successfully with ID: " + savedProduct.getId());
            
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE_ATTR, "Product added successfully!");
            return REDIRECT_PRODUCTS;
            
        } catch (Exception e) {
            System.err.println("[AdminProductController] addProduct: Error adding product");
            e.printStackTrace(System.err);
            
            model.addAttribute(ERROR_MESSAGE_ATTR, "Error adding product. Please try again.");
            model.addAttribute(PRODUCT_ATTR, product);
            model.addAttribute(CATEGORIES_ATTR, categoryService.findAllCategories());
            model.addAttribute(IS_EDIT_ATTR, false);
            return FORM_VIEW;
        }
    }
    
    /**
     * Show form to edit an existing product
     */
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Integer productId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<ProductDTO> productOpt = productService.findProductById(productId);
            
            if (productOpt.isPresent()) {
                ProductDTO product = productOpt.get();
                model.addAttribute(PRODUCT_ATTR, product);
                model.addAttribute(CATEGORIES_ATTR, categoryService.findAllCategories());
                model.addAttribute(TITLE_ATTR, "Admin - Edit Product: " + product.getName());
                model.addAttribute(IS_EDIT_ATTR, true);
                
                return FORM_VIEW;
            } else {
                System.out.println("[AdminProductController] showEditProductForm: Product not found with ID: " + productId);
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE_ATTR, "Product not found");
                return REDIRECT_PRODUCTS;
            }
        } catch (Exception e) {
            System.err.println("[AdminProductController] showEditProductForm: Error getting product with ID: " + productId);
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE_ATTR, "Error loading product");
            return REDIRECT_PRODUCTS;
        }
    }
    
    /**
     * Process form submission to update an existing product
     */
    @PostMapping("/update")
    public String updateProduct(@ModelAttribute ProductDTO product, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Validate required fields
            if (product.getName() == null || product.getName().trim().isEmpty() || 
                product.getThumbnailUrl() == null || product.getThumbnailUrl().trim().isEmpty()) {
                
                model.addAttribute(ERROR_MESSAGE_ATTR, "Product name and thumbnail URL are required");
                model.addAttribute(PRODUCT_ATTR, product);
                model.addAttribute(CATEGORIES_ATTR, categoryService.findAllCategories());
                model.addAttribute(IS_EDIT_ATTR, true);
                return FORM_VIEW;
            }
            
            // Check if product exists
            if (!productService.productExistsById(product.getId())) {
                System.err.println("[AdminProductController] updateProduct: Product not found with ID: " + product.getId());
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE_ATTR, "Product not found");
                return REDIRECT_PRODUCTS;
            }
            
            // Set category if categoryId is provided
            if (product.getCategoryId() != null && product.getCategoryId() > 0) {
                Optional<CategoryDTO> categoryOpt = categoryService.findCategoryById(product.getCategoryId());
                if (categoryOpt.isPresent()) {
                    product.setCategory(categoryOpt.get());
                }
            } else {
                product.setCategory(null);
            }
            
            // Update product
            productService.saveProduct(product);
            System.out.println("[AdminProductController] updateProduct: Product updated successfully with ID: " + product.getId());
            
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE_ATTR, "Product updated successfully!");
            return REDIRECT_PRODUCTS;
            
        } catch (Exception e) {
            System.err.println("[AdminProductController] updateProduct: Error updating product");
            e.printStackTrace(System.err);
            
            model.addAttribute(ERROR_MESSAGE_ATTR, "Error updating product. Please try again.");
            model.addAttribute(PRODUCT_ATTR, product);
            model.addAttribute(CATEGORIES_ATTR, categoryService.findAllCategories());
            model.addAttribute(IS_EDIT_ATTR, true);
            return FORM_VIEW;
        }
    }
    
    /**
     * Delete a product
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public String deleteProduct(@PathVariable("id") Integer productId) {
        try {
            // Check if product exists
            if (!productService.productExistsById(productId)) {
                return "error:Product not found";
            }
            
            // Delete product
            productService.deleteProductById(productId);
            System.out.println("[AdminProductController] deleteProduct: Product deleted successfully with ID: " + productId);
            
            return "success:Product deleted successfully!";
            
        } catch (Exception e) {
            System.err.println("[AdminProductController] deleteProduct: Error deleting product with ID: " + productId);
            e.printStackTrace(System.err);
            return "error:Error deleting product. Please try again.";
        }
    }
}
