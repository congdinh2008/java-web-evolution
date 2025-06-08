package com.congdinh.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.congdinh.dto.UserDTO;
import com.congdinh.models.User;
import com.congdinh.services.UserService;
import com.congdinh.services.RoleService;

import jakarta.validation.Valid;

/**
 * Controller for handling authentication-related requests
 * Includes login, logout, registration, and dashboard redirects
 */
@Controller
public class AuthController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AuthController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * Display login page
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully!");
        }
        
        // Check if user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        
        return "auth/login";
    }

    /**
     * Display registration page
     */
    @GetMapping("/register")
    public String register(Model model) {
        // Check if user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("user", new UserDTO());
        return "auth/register";
    }

    /**
     * Process registration form
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDTO userDTO,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        
        try {
            // Check if user with email already exists
            if (userDTO.getEmail() != null && userService.existsByEmail(userDTO.getEmail())) {
                model.addAttribute("error", "A user with this email already exists!");
                return "auth/register";
            }
            
            // Check if user with phone number already exists
            if (userService.existsByPhoneNumber(userDTO.getPhoneNumber())) {
                model.addAttribute("error", "A user with this phone number already exists!");
                return "auth/register";
            }
            
            // Create new user with default USER role
            User newUser = userService.createUser(userDTO);
            
            redirectAttributes.addFlashAttribute("success", 
                "Registration successful! You can now login with your credentials.");
            return "redirect:/login";
            
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }

    /**
     * Dashboard redirect based on user role
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            // Redirect based on user roles
            if (auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/admin/dashboard";
            } else if (auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_MODERATOR"))) {
                return "redirect:/moderator/dashboard";
            } else {
                return "redirect:/user/dashboard";
            }
        }
        
        return "redirect:/login";
    }

    /**
     * Admin dashboard
     */
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        
        // Add user information
        model.addAttribute("user", currentUser);
        
        // Add statistics for dashboard
        model.addAttribute("totalUsers", userService.getTotalUsers());
        model.addAttribute("totalRoles", roleService.getTotalRoles());
        
        // Mock data for products and categories (replace with actual service calls when available)
        model.addAttribute("totalProducts", 0);
        model.addAttribute("totalCategories", 0);
        model.addAttribute("lowStockProducts", 0);
        
        return "admin/dashboard";
    }

    /**
     * Moderator dashboard
     */
    @GetMapping("/moderator/dashboard")
    public String moderatorDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        
        // Add user information
        model.addAttribute("user", currentUser);
        
        // Add statistics for moderator dashboard
        model.addAttribute("totalUsers", userService.getTotalUsers());
        
        // Mock data for moderator-specific stats (replace with actual service calls when available)
        model.addAttribute("pendingProducts", 0);
        model.addAttribute("totalProducts", 0);
        model.addAttribute("totalCategories", 0);
        model.addAttribute("lowStockProducts", 0);
        
        return "moderator/dashboard";
    }

    /**
     * User dashboard
     */
    @GetMapping("/user/dashboard")
    public String userDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        
        // Add user information
        model.addAttribute("user", currentUser);
        
        // Mock data for user-specific stats (replace with actual service calls when available)
        model.addAttribute("totalOrders", 0);
        model.addAttribute("pendingOrders", 0);
        model.addAttribute("wishlistItems", 0);
        model.addAttribute("totalAddresses", 1);
        
        return "user/dashboard";
    }
}
