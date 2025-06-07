package com.congdinh.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.congdinh.dto.ProductDTO;
import com.congdinh.services.ProductService;

/**
 * Controller for handling product-related operations
 */
@Controller
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * List all products
     */
    @GetMapping
    public String listProducts(Model model) {
        try {
            List<ProductDTO> products = productService.findAllProducts();
            model.addAttribute("products", products);
            model.addAttribute("title", "ViVu Store - Our Products");
            System.out.println("[ProductController] listProducts: Found " + products.size() + " products");
            
            return "product/list";
        } catch (Exception e) {
            System.err.println("[ProductController] listProducts: Error getting products");
            e.printStackTrace(System.err);
            
            model.addAttribute("message", "Error: Could not load products.");
            model.addAttribute("description", "Please try again later or contact support.");
            model.addAttribute("products", new ArrayList<ProductDTO>());
            
            return "home/index";
        }
    }
    
    /**
     * View product details
     */
    @GetMapping("/details")
    public String viewProductDetails(@RequestParam("id") Integer productId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<ProductDTO> productOpt = productService.findProductById(productId);
            
            if (productOpt.isPresent()) {
                ProductDTO product = productOpt.get();
                model.addAttribute("product", product);
                model.addAttribute("title", "ViVu Store - " + product.getName());
                
                return "product/details";
            } else {
                // Product not found, redirect to products page
                System.out.println("[ProductController] viewProductDetails: Product not found with ID: " + productId);
                return "redirect:/products";
            }
        } catch (Exception e) {
            System.err.println("[ProductController] viewProductDetails: Error getting product with ID: " + productId);
            e.printStackTrace(System.err);
            return "redirect:/products";
        }
    }
    
    /**
     * Show form to add a new product
     */
    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("title", "ViVu Store - Add New Product");
        // Create an empty DTO for the form with ID explicitly set to 0
        ProductDTO newProduct = new ProductDTO();
        newProduct.setId(0); // Explicitly set ID to 0 for new products
        model.addAttribute("product", newProduct);
        return "product/form";
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
                
                model.addAttribute("errorMessage", "Product name and thumbnail URL are required");
                return "product/form";
            }
            
            // Save new product
            ProductDTO savedProduct = productService.saveProduct(product);
            System.out.println("[ProductController] addProduct: Product added successfully with ID: " + savedProduct.getId());
            
            // Redirect to products list
            return "redirect:/products";
            
        } catch (Exception e) {
            System.err.println("[ProductController] addProduct: Error adding product");
            e.printStackTrace(System.err);
            
            model.addAttribute("errorMessage", "Error adding product. Please try again.");
            return "product/form";
        }
    }
    
    /**
     * Show form to edit an existing product
     */
    @GetMapping("/edit")
    public String showEditProductForm(@RequestParam("id") Integer productId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<ProductDTO> productOpt = productService.findProductById(productId);
            
            if (productOpt.isPresent()) {
                ProductDTO product = productOpt.get();
                model.addAttribute("product", product);
                model.addAttribute("title", "ViVu Store - Edit " + product.getName());
                
                return "product/form";
            } else {
                // Product not found, redirect to products page
                System.out.println("[ProductController] showEditProductForm: Product not found with ID: " + productId);
                return "redirect:/products";
            }
        } catch (Exception e) {
            System.err.println("[ProductController] showEditProductForm: Error getting product with ID: " + productId);
            e.printStackTrace(System.err);
            return "redirect:/products";
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
                
                model.addAttribute("errorMessage", "Product name and thumbnail URL are required");
                return "product/form";
            }
            
            // Check if product exists
            if (!productService.productExistsById(product.getId())) {
                System.err.println("[ProductController] updateProduct: Product not found with ID: " + product.getId());
                return "redirect:/products";
            }
            
            // Update product
            productService.saveProduct(product);
            System.out.println("[ProductController] updateProduct: Product updated successfully with ID: " + product.getId());
            
            // Redirect to products list
            return "redirect:/products";
            
        } catch (Exception e) {
            System.err.println("[ProductController] updateProduct: Error updating product");
            e.printStackTrace(System.err);
            
            model.addAttribute("errorMessage", "Error updating product. Please try again.");
            model.addAttribute("product", product);
            return "product/form";
        }
    }
    
    /**
     * Delete a product
     */
    @PostMapping("/delete")
    public String deleteProduct(@RequestParam("id") Integer productId, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProductById(productId);
            System.out.println("[ProductController] deleteProduct: Product deleted successfully with ID: " + productId);
            
            // Redirect to products list
            return "redirect:/products";
            
        } catch (Exception e) {
            System.err.println("[ProductController] deleteProduct: Error deleting product");
            e.printStackTrace(System.err);
            return "redirect:/products";
        }
    }
    
    /**
     * Search products by name
     */
    @GetMapping("/search")
    public String searchProducts(@RequestParam(required = false) String name,
                                @RequestParam(required = false) Double minPrice,
                                @RequestParam(required = false) Double maxPrice,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "name") String sortBy,
                                @RequestParam(defaultValue = "ASC") String direction,
                                Model model) {
        try {
            Page<ProductDTO> productPage;
            List<ProductDTO> products;
            
            // If name is provided, search by name
            if (name != null && !name.trim().isEmpty()) {
                productPage = productService.findPaginatedProductsByName(name, page, size);
                model.addAttribute("searchTerm", name);
            } 
            // If price range is provided, search by price range
            else if (minPrice != null && maxPrice != null) {
                products = productService.findProductsInPriceRange(minPrice, maxPrice);
                model.addAttribute("products", products);
                model.addAttribute("minPrice", minPrice);
                model.addAttribute("maxPrice", maxPrice);
                model.addAttribute("title", "ViVu Store - Products $" + minPrice + " - $" + maxPrice);
                return "products";
            }
            // Otherwise, return all products with pagination
            else {
                productPage = productService.findPaginatedProducts(page, size, sortBy, direction);
            }
            
            model.addAttribute("productPage", productPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("totalItems", productPage.getTotalElements());
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("direction", direction);
            model.addAttribute("title", "ViVu Store - Search Results");
            
            return "product/search";
        } catch (Exception e) {
            System.err.println("[ProductController] searchProducts: Error searching products");
            e.printStackTrace(System.err);
            
            model.addAttribute("message", "Error: Could not search products.");
            model.addAttribute("description", "Please try again later or contact support.");
            return "error";
        }
    }
    
    /**
     * Get products with low stock for inventory management
     */
    @GetMapping("/low-stock")
    public String getLowStockProducts(@RequestParam(defaultValue = "5") int threshold, Model model) {
        try {
            List<ProductDTO> products = productService.findProductsWithLowStock(threshold);
            model.addAttribute("products", products);
            model.addAttribute("threshold", threshold);
            model.addAttribute("title", "ViVu Store - Low Stock Products");
            
            return "product/low-stock";
        } catch (Exception e) {
            System.err.println("[ProductController] getLowStockProducts: Error getting low stock products");
            e.printStackTrace(System.err);
            
            return "redirect:/products";
        }
    }
}
