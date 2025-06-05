package com.congdinh.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.annotation.PostConstruct;

import com.congdinh.models.Product;
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
     * Initialize database after controller construction
     * Wrapped in try-catch to handle any transaction or database initialization issues
     */
    @PostConstruct
    public void init() {
        try {
            // Create the Products table if it does not exist
            this.productService.createProductsTableIfNotExists();
            System.out.println("[ProductController] init: Products table checked/created.");
            
            // Optionally, initialize sample data if the table is empty
            this.productService.initializeSampleData();
            System.out.println("[ProductController] init: Sample data initialization checked.");
        } catch (Exception e) {
            System.err.println("[ProductController] init: Error during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * List all products
     */
    @GetMapping
    public String listProducts(Model model) {
        try {
            List<Product> products = productService.findAllProducts();
            model.addAttribute("products", products);
            model.addAttribute("title", "ViVu Store - Our Products");
            System.out.println("[ProductController] listProducts: Found " + products.size() + " products");
            
            return "products";
        } catch (Exception e) {
            System.err.println("[ProductController] listProducts: Error getting products");
            e.printStackTrace(System.err);
            
            model.addAttribute("message", "Error: Could not load products.");
            model.addAttribute("description", "Please try again later or contact support.");
            model.addAttribute("products", new ArrayList<Product>());
            
            return "index";
        }
    }
    
    /**
     * View product details
     */
    @GetMapping("/details")
    public String viewProductDetails(@RequestParam("id") Integer productId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Product> productOpt = productService.findProductById(productId);
            
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                model.addAttribute("product", product);
                model.addAttribute("title", "ViVu Store - " + product.getName());
                
                return "product-details";
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
        // No need to add empty product - the form handles this case
        return "product-form";
    }
    
    /**
     * Process form submission to add a new product
     */
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Validate required fields
            if (product.getName() == null || product.getName().trim().isEmpty() || 
                product.getThumbnailUrl() == null || product.getThumbnailUrl().trim().isEmpty()) {
                
                model.addAttribute("errorMessage", "Product name and thumbnail URL are required");
                return "product-form";
            }
            
            // Save new product
            Product savedProduct = productService.saveProduct(product);
            System.out.println("[ProductController] addProduct: Product added successfully with ID: " + savedProduct.getId());
            
            // Redirect to products list
            return "redirect:/products";
            
        } catch (Exception e) {
            System.err.println("[ProductController] addProduct: Error adding product");
            e.printStackTrace(System.err);
            
            model.addAttribute("errorMessage", "Error adding product. Please try again.");
            return "product-form";
        }
    }
    
    /**
     * Show form to edit an existing product
     */
    @GetMapping("/edit")
    public String showEditProductForm(@RequestParam("id") Integer productId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Product> productOpt = productService.findProductById(productId);
            
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                model.addAttribute("product", product);
                model.addAttribute("title", "ViVu Store - Edit " + product.getName());
                
                return "product-form";
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
    public String updateProduct(@ModelAttribute Product product, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Validate required fields
            if (product.getName() == null || product.getName().trim().isEmpty() || 
                product.getThumbnailUrl() == null || product.getThumbnailUrl().trim().isEmpty()) {
                
                model.addAttribute("errorMessage", "Product name and thumbnail URL are required");
                return "product-form";
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
            return "product-form";
        }
    }
    
    /**
     * Delete a product
     */
    @PostMapping("/delete")
    public String deleteProduct(@RequestParam("id") Integer productId, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = productService.deleteProductById(productId);
            
            if (deleted) {
                System.out.println("[ProductController] deleteProduct: Product deleted successfully with ID: " + productId);
            } else {
                System.err.println("[ProductController] deleteProduct: Failed to delete product with ID: " + productId);
            }
            
            // Redirect to products list
            return "redirect:/products";
            
        } catch (Exception e) {
            System.err.println("[ProductController] deleteProduct: Error deleting product");
            e.printStackTrace(System.err);
            return "redirect:/products";
        }
    }
}
