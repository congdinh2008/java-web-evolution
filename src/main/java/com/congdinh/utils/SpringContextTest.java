package com.congdinh.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.congdinh.config.AppConfig;
import com.congdinh.services.ProductService;

/**
 * Simple class to test Spring context loading with annotation-based configuration
 */
public class SpringContextTest {
    
    public static void main(String[] args) {
        try {
            System.out.println("Testing Spring context loading with annotation-based configuration...");
            
            // Load Spring application context using Java configuration
            @SuppressWarnings("resource") // The context doesn't need to be closed in this test
            ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
            
            System.out.println("Spring context loaded successfully!");
            
            // Get the ProductService bean
            ProductService productService = context.getBean(ProductService.class);
            System.out.println("ProductService bean retrieved successfully!");
            
            // Test a simple operation to verify the service works
            int productCount = productService.findAllProducts().size();
            System.out.println("Current product count: " + productCount);
            
            System.out.println("Spring integration test completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Error during Spring context test: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
