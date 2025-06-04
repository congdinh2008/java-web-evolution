package com.congdinh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.congdinh.models.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

public class ViVuStoreApplication extends HttpServlet {
    private List<Product> productList;
    
    @Override
    public void init() throws ServletException {
        // Initialize the product list with sample data
        productList = new ArrayList<>();
        productList.add(new Product(1, "Smartphone X", 799.99, 15, "https://placehold.co/600x400/png"));
        productList.add(new Product(2, "Laptop Pro", 1299.99, 10, "https://placehold.co/600x400/png"));
        productList.add(new Product(3, "Wireless Headphones", 199.99, 25, "https://placehold.co/600x400/png"));
        productList.add(new Product(4, "Smart Watch", 299.99, 20, "https://placehold.co/600x400/png"));
        productList.add(new Product(5, "4K Smart TV", 899.99, 5, "https://placehold.co/600x400/png"));
        productList.add(new Product(6, "Bluetooth Speaker", 149.99, 30, "https://placehold.co/600x400/png"));
        productList.add(new Product(7, "Digital Camera", 599.99, 8, "https://placehold.co/600x400/png"));
        productList.add(new Product(8, "Gaming Console", 499.99, 12, "https://placehold.co/600x400/png"));
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[ViVuStoreApplication] doGet: Entered method. Path: " + request.getPathInfo()); // DEBUG

        String path = request.getServletPath();
        System.out.println("[ViVuStoreApplication] doGet: Path: " + path); // DEBUG
        
        // Set common attributes for all pages
        request.setAttribute("title", "ViVu Store - Home Page");
        
        // Route to the appropriate JSP based on the path
        String jspPath;
        switch (path) {
            case "/products":
                request.setAttribute("title", "ViVu Store - Our Products");
                request.setAttribute("products", productList);
                jspPath = "/products.jsp";
                break;
                
            case "/about":
                request.setAttribute("title", "ViVu Store - About Us");
                jspPath = "/about.jsp";
                break;
                
            case "/contact":
                request.setAttribute("title", "ViVu Store - Contact Us");
                jspPath = "/contact.jsp";
                break;
                
            default:
                // Home page or any other path defaults to index
                String messageValue = "Welcome to ViVu Store!";
                request.setAttribute("message", messageValue);
                request.setAttribute("description", "Your one-stop shop for all things ViVu.");
                request.setAttribute("keywords", "ViVu, Store, Shopping, Online Store");
                jspPath = "/index.jsp";
                break;
        }

        System.out.println("[ViVuStoreApplication] doGet: Forwarding to " + jspPath); // DEBUG
        
        // Forward the request to the appropriate JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher(jspPath);
        try {
            dispatcher.forward(request, response);
            System.out.println("[ViVuStoreApplication] doGet: Forward completed successfully"); // DEBUG
        } catch (Exception e) {
            System.err.println("[ViVuStoreApplication] doGet: EXCEPTION DURING FORWARD!"); // DEBUG
            e.printStackTrace(System.err); // DEBUG
            throw e; // Re-throw the exception so Tomcat can handle it
        }
    }
}