package com.congdinh.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Base controller for handling general pages
 */
@Controller
public class HomeController {
    
    /**
     * Home page
     */
    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("title", "ViVu Store - Home Page");
        model.addAttribute("message", "Welcome to ViVu Store!");
        model.addAttribute("description", "Your one-stop shop for all things ViVu.");
        return "home/index";
    }
    
    /**
     * About page
     */
    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("title", "ViVu Store - About Us");
        return "home/about";
    }
    
    /**
     * Contact page
     */
    @GetMapping("/contact")
    public String contactPage(Model model) {
        model.addAttribute("title", "ViVu Store - Contact Us");
        return "home/contact";
    }
}
