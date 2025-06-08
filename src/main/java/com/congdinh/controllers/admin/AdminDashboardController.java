package com.congdinh.controllers.admin;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.congdinh.services.ProductService;
import com.congdinh.services.RoleService;
import com.congdinh.services.UserService;
import com.congdinh.models.User;
import com.congdinh.services.CategoryService;

/**
 * Admin dashboard controller
 */
@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final RoleService roleService;

    public AdminDashboardController(ProductService productService, CategoryService categoryService,
            UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
        this.productService = productService;
        this.categoryService = categoryService;
    }

    /**
     * Admin dashboard home
     */
    @GetMapping
    public String dashboard(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) auth.getPrincipal();

            // Get statistics using the new service methods
            long totalProducts = productService.getTotalProductCount();
            long totalCategories = categoryService.getTotalCategoryCount();
            long activeProducts = productService.getActiveProductCount();
            long lowStockProducts = productService.getLowStockProductCount();
            long outOfStockProducts = productService.getOutOfStockProductCount();

            // Add user information
            model.addAttribute("user", currentUser);

            // Add statistics for dashboard
            model.addAttribute("totalUsers", userService.getTotalUsers());
            model.addAttribute("totalRoles", roleService.getTotalRoles());
            model.addAttribute("title", "Admin Dashboard");
            model.addAttribute("totalProducts", totalProducts);
            model.addAttribute("totalCategories", totalCategories);
            model.addAttribute("activeProducts", activeProducts);
            model.addAttribute("lowStockProducts", lowStockProducts);
            model.addAttribute("outOfStockProducts", outOfStockProducts);

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
