package com.congdinh.utils;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.congdinh.models.Product;
import com.congdinh.repositories.ProductJPARepository;
import com.congdinh.services.RoleService;
import com.congdinh.services.UserService;
import com.congdinh.dto.UserRegistrationDto;

import java.time.LocalDate;

/**
 * Component to seed initial data into the database
 * Uses event listener to run after application startup
 */
@Component
public class SeedData {
    
    private final ProductJPARepository productRepository;
    private final RoleService roleService;
    private final UserService userService;
    
    @Autowired
    public SeedData(ProductJPARepository productRepository, RoleService roleService, UserService userService) {
        this.productRepository = productRepository;
        this.roleService = roleService;
        this.userService = userService;
    }
    
    /**
     * Initialize sample data when the application context is fully loaded
     */
    @EventListener
    @Transactional
    public void seed(ContextRefreshedEvent event) {
        initializeRoles();
        initializeSampleData();
        initializeAdminUser();
    }
    
    /**
     * Initialize sample data if the table is empty
     */
    private void initializeSampleData() {
        // Check if we have any products
        long count = productRepository.count();
        
        if (count == 0) {
            System.out.println("[SeedData] initializeSampleData: Adding sample data");
            
            // Add some sample products
            List<Product> sampleProducts = Arrays.asList(
                new Product(0, "Smartphone X", 799.99, 50, "https://placehold.co/600x400?text=Smartphone+X"),
                new Product(0, "Laptop Pro", 1299.99, 25, "https://placehold.co/600x400?text=Laptop+Pro"),
                new Product(0, "Wireless Earbuds", 129.99, 100, "https://placehold.co/600x400?text=Wireless+Earbuds"),
                new Product(0, "Smart Watch", 249.99, 30, "https://placehold.co/600x400?text=Smart+Watch"),
                new Product(0, "4K TV", 899.99, 15, "https://placehold.co/600x400?text=4K+TV")
            );
            
            // Insert all products at once
            productRepository.saveAll(sampleProducts);
            
            System.out.println("[SeedData] initializeSampleData: Added " + sampleProducts.size() + " sample products");
        } else {
            System.out.println("[SeedData] initializeSampleData: Table already has data, skipping");
        }
    }
    
    /**
     * Initialize default roles if they don't exist
     */
    private void initializeRoles() {
        System.out.println("[SeedData] initializeRoles: Creating default roles");
        roleService.createDefaultRoles();
        System.out.println("[SeedData] initializeRoles: Default roles created successfully");
    }
    
    /**
     * Initialize admin user if it doesn't exist
     */
    private void initializeAdminUser() {
        String adminPhoneNumber = "0123456789";
        
        if (!userService.existsByPhoneNumber(adminPhoneNumber)) {
            System.out.println("[SeedData] initializeAdminUser: Creating admin user");
            
            try {
                UserRegistrationDto adminUser = new UserRegistrationDto(
                    "Admin",
                    "User",
                    adminPhoneNumber,
                    "admin123",
                    "admin123",
                    LocalDate.of(1990, 1, 1),
                    "Admin Address"
                );
                
                userService.registerUser(adminUser);
                
                // Add admin role to the user
                Long userId = userService.findUserByPhoneNumber(adminPhoneNumber).get().getId();
                Long adminRoleId = roleService.findRoleByName("ROLE_ADMIN").get().getId();
                userService.addRoleToUser(userId, adminRoleId);
                
                System.out.println("[SeedData] initializeAdminUser: Admin user created successfully");
                System.out.println("[SeedData] initializeAdminUser: Admin credentials - Phone: " + adminPhoneNumber + ", Password: admin123");
            } catch (Exception e) {
                System.err.println("[SeedData] initializeAdminUser: Failed to create admin user: " + e.getMessage());
            }
        } else {
            System.out.println("[SeedData] initializeAdminUser: Admin user already exists, skipping");
        }
    }
}
