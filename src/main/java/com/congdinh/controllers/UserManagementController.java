package com.congdinh.controllers;

import com.congdinh.dto.UserDto;
import com.congdinh.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controller for user management operations (Admin only)
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {
    
    private final UserService userService;
    
    @Autowired
    public UserManagementController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Display all users (Admin only)
     */
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("title", "ViVu Store - User Management");
        
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        
        return "admin/users";
    }
    
    /**
     * Display user details (Admin only)
     */
    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        model.addAttribute("title", "ViVu Store - User Details");
        
        UserDto user = userService.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        
        return "admin/user-details";
    }
}