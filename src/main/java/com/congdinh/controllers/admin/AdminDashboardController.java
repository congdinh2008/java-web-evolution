package com.congdinh.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.congdinh.services.ProductService;
import com.congdinh.services.CategoryService;

/**
 * Admin dashboard controller
 */
@Controller
@RequestMapping("/admin")
public class AdminDashboardController {
    
    private final ProductService productService;
    private final CategoryService categoryService;
    
    public AdminDashboardController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }
    
    /**
     * Admin dashboard home
     */
    @GetMapping
    public String dashboard(Model model) {
        try {
            // Get basic statistics
            long totalProducts = productService.findAllProducts().size();
            long totalCategories = categoryService.findAllCategories().size();
            long lowStockProducts = productService.findProductsWithLowStock(5).size();
            
            model.addAttribute("title", "Admin Dashboard");
            model.addAttribute("totalProducts", totalProducts);
            model.addAttribute("totalCategories", totalCategories);
            model.addAttribute("lowStockProducts", lowStockProducts);
            
            return "admin/dashboard";
            
        } catch (Exception e) {
            System.err.println("[AdminDashboardController] dashboard: Error loading dashboard");
            e.printStackTrace(System.err);
            
            model.addAttribute("title", "Admin Dashboard");
            model.addAttribute("errorMessage", "Error loading dashboard data");
            return "admin/dashboard";
        }
    }
}
