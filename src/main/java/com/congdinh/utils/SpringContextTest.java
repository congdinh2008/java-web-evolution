package com.congdinh.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.congdinh.services.ProductService;

/**
 * Simple class to test Spring context loading
 */
public class SpringContextTest {

    public static void main(String[] args) {
        try {
            System.out.println("Testing Spring context loading...");

            // Load Spring application context
            ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

            // Check that the context is not null
            if (context != null) {
                System.out.println("Spring context loaded successfully!");

                // Check that we can get beans from the context
                ProductService productService = context.getBean("productService", ProductService.class);

                if (productService != null) {
                    System.out.println("ProductService bean retrieved successfully!");

                    // Test a simple operation to verify the service works
                    int productCount = productService.findAllProducts().size();
                    System.out.println("Current product count: " + productCount);

                    System.out.println("Spring integration test completed successfully!");
                } else {
                    System.err.println("Failed to retrieve ProductService bean from context");
                }
            } else {
                System.err.println("Failed to load Spring context");
            }

        } catch (Exception e) {
            System.err.println("Error during Spring context test: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
