package com.congdinh.controllers;

import com.congdinh.dto.UserRegistrationDto;
import com.congdinh.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling authentication related requests
 */
@Controller
public class AuthController {
    
    private final UserService userService;
    
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Display login page
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        model.addAttribute("title", "ViVu Store - Login");
        
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid phone number or password.");
        }
        
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        
        return "auth/login";
    }
    
    /**
     * Display registration page
     */
    @GetMapping("/register")
    public String registrationPage(Model model) {
        model.addAttribute("title", "ViVu Store - Register");
        model.addAttribute("userRegistration", new UserRegistrationDto());
        return "auth/register";
    }
    
    /**
     * Handle user registration
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userRegistration") UserRegistrationDto registrationDto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        model.addAttribute("title", "ViVu Store - Register");
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        
        try {
            // Check if phone number already exists
            if (userService.existsByPhoneNumber(registrationDto.getPhoneNumber())) {
                bindingResult.rejectValue("phoneNumber", "error.phoneNumber", "Phone number already exists");
                return "auth/register";
            }
            
            // Check if passwords match
            if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
                return "auth/register";
            }
            
            // Register the user
            userService.registerUser(registrationDto);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Registration successful! You can now log in with your credentials.");
            return "redirect:/login";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }
}