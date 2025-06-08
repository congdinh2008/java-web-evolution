package com.congdinh.utils;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.congdinh.models.Product;
import com.congdinh.models.Role;
import com.congdinh.models.User;
import com.congdinh.repositories.ProductJPARepository;
import com.congdinh.repositories.RoleRepository;
import com.congdinh.repositories.UserRepository;

/**
 * Component to seed initial data into the database
 * Uses event listener to run after application startup
 */
@Component
public class SeedData {
    
    private final ProductJPARepository productRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public SeedData(ProductJPARepository productRepository, 
                   UserRepository userRepository, 
                   RoleRepository roleRepository, 
                   PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Initialize sample data when the application context is fully loaded
     */
    @EventListener
    @Transactional
    public void seed(ContextRefreshedEvent event) {
        initializeSampleData();
    }
    
    /**
     * Initialize sample data if the table is empty
     */
    private void initializeSampleData() {
        // Initialize roles first
        initializeRoles();
        
        // Initialize users with roles
        initializeUsers();
        
        // Initialize products
        initializeProducts();
    }
    
    /**
     * Initialize default roles
     */
    private void initializeRoles() {
        long roleCount = roleRepository.count();
        
        if (roleCount == 0) {
            System.out.println("[SeedData] initializeRoles: Adding default roles");
            
            List<Role> defaultRoles = Arrays.asList(
                new Role("ADMIN", "System Administrator - Full access to all features"),
                new Role("USER", "Regular User - Basic access to user features"),
                new Role("MODERATOR", "Content Moderator - Can manage content and users")
            );
            
            roleRepository.saveAll(defaultRoles);
            System.out.println("[SeedData] initializeRoles: Added " + defaultRoles.size() + " default roles");
        } else {
            System.out.println("[SeedData] initializeRoles: Roles already exist, skipping");
        }
    }
    
    /**
     * Initialize default users with roles
     */
    private void initializeUsers() {
        long userCount = userRepository.count();
        
        if (userCount == 0) {
            System.out.println("[SeedData] initializeUsers: Adding default users");
            
            // Get roles
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            Role userRole = roleRepository.findByName("USER").orElseThrow();
            Role moderatorRole = roleRepository.findByName("MODERATOR").orElseThrow();
            
            // Create admin user
            User admin = new User(
                "admin", 
                "Admin", 
                "User", 
                "+1234567890", 
                "admin@example.com", 
                passwordEncoder.encode("admin123"), 
                "123 Admin Street, Admin City, AC 12345"
            );
            admin.addRole(adminRole);
            
            // Create regular user
            User regularUser = new User(
                "johndoe", 
                "John", 
                "Doe", 
                "+1987654321", 
                "john.doe@example.com", 
                passwordEncoder.encode("user123"), 
                "456 User Avenue, User City, UC 67890"
            );
            regularUser.addRole(userRole);
            
            // Create moderator user
            User moderator = new User(
                "janesmith", 
                "Jane", 
                "Smith", 
                "+1555123456", 
                "jane.smith@example.com", 
                passwordEncoder.encode("mod123"), 
                "789 Moderator Lane, Mod City, MC 54321"
            );
            moderator.addRole(moderatorRole);
            moderator.addRole(userRole); // Moderator can also have user privileges
            
            // Create user without email (optional email field)
            User noEmailUser = new User(
                "bobwilson", 
                "Bob", 
                "Wilson", 
                "+1444555666", 
                null, // No email
                passwordEncoder.encode("bob123"), 
                "321 No Email Road, NE City, NE 98765"
            );
            noEmailUser.addRole(userRole);
            
            List<User> defaultUsers = Arrays.asList(admin, regularUser, moderator, noEmailUser);
            userRepository.saveAll(defaultUsers);
            
            System.out.println("[SeedData] initializeUsers: Added " + defaultUsers.size() + " default users");
        } else {
            System.out.println("[SeedData] initializeUsers: Users already exist, skipping");
        }
    }
    
    /**
     * Initialize sample products
     */
    private void initializeProducts() {
        // Check if we have any products
        long count = productRepository.count();
        
        if (count == 0) {
            System.out.println("[SeedData] initializeProducts: Adding sample products");
            
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
            
            System.out.println("[SeedData] initializeProducts: Added " + sampleProducts.size() + " sample products");
        } else {
            System.out.println("[SeedData] initializeProducts: Products already exist, skipping");
        }
    }
}
