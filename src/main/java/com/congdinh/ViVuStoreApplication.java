package com.congdinh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.congdinh.models.Product;
import com.congdinh.services.ProductService;
import com.congdinh.utils.SpringContextLoaderListener;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Main Servlet Application for ViVu Store
 */
public class ViVuStoreApplication extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ProductService productService;

    @Override
    public void init() throws ServletException {
        // Get the ProductService bean from Spring ApplicationContext
        try {
            productService = SpringContextLoaderListener.getBean(ProductService.class);
            System.out.println("[ViVuStoreApplication] init: Obtained ProductService from Spring ApplicationContext");
        } catch (Exception e) {
            System.err.println("[ViVuStoreApplication] init: Error getting ProductService from Spring context");
            e.printStackTrace(System.err);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[ViVuStoreApplication] doGet: Entered method. Path: " + request.getServletPath());

        String path = request.getServletPath();
        String jspPath;

        switch (path) {
            case "/products":
                handleProductsPage(request, response);
                return; // handleProductsPage handles the forward

            case "/about":
                request.setAttribute("title", "ViVu Store - About Us");
                jspPath = "/about.jsp";
                break;

            case "/contact":
                request.setAttribute("title", "ViVu Store - Contact Us");
                jspPath = "/contact.jsp";
                break;

            case "/product-details":
                handleProductDetails(request, response);
                return; // handleProductDetails handles the forward

            case "/products/add":
                // Display form for adding a new product
                request.setAttribute("title", "ViVu Store - Add New Product");
                forwardToPage(request, response, "/product-form.jsp");
                return;

            case "/products/edit":
                handleEditProductForm(request, response);
                return;

            default:
                // Home page or any other path defaults to index
                request.setAttribute("title", "ViVu Store - Home Page");
                request.setAttribute("message", "Welcome to ViVu Store!");
                request.setAttribute("description", "Your one-stop shop for all things ViVu.");
                jspPath = "/index.jsp";
                break;
        }

        System.out.println("[ViVuStoreApplication] doGet: Forwarding to " + jspPath);
        forwardToPage(request, response, jspPath);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        System.out.println("[ViVuStoreApplication] doPost: Entered method. Path: " + path);

        switch (path) {
            case "/products/add":
                handleAddProduct(request, response);
                break;

            case "/products/update":
                handleUpdateProduct(request, response);
                break;

            case "/products/delete":
                handleDeleteProduct(request, response);
                break;

            default:
                // If no specific handler, default to GET handling
                doGet(request, response);
                break;
        }
    }

    /**
     * Handle products listing page
     */
    private void handleProductsPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("title", "ViVu Store - Our Products");

        try {
            List<Product> products = productService.findAllProducts();
            request.setAttribute("products", products);
            System.out.println("[ViVuStoreApplication] handleProductsPage: Found " + products.size() + " products");

            forwardToPage(request, response, "/products.jsp");
        } catch (Exception e) {
            System.err.println("[ViVuStoreApplication] handleProductsPage: Error getting products");
            e.printStackTrace(System.err);

            request.setAttribute("message", "Error: Could not load products.");
            request.setAttribute("description", "Please try again later or contact support.");
            request.setAttribute("products", new ArrayList<Product>());

            forwardToPage(request, response, "/index.jsp");
        }
    }

    /**
     * Handle product details page
     */
    private void handleProductDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productIdParam = request.getParameter("id");

        if (productIdParam == null || productIdParam.isEmpty()) {
            // Redirect to products page if no ID provided
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        try {
            int productId = Integer.parseInt(productIdParam);
            Optional<Product> productOpt = productService.findProductById(productId);

            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                request.setAttribute("product", product);
                request.setAttribute("title", "ViVu Store - " + product.getName());

                forwardToPage(request, response, "/product-details.jsp");
            } else {
                // Product not found, redirect to products page
                System.out.println(
                        "[ViVuStoreApplication] handleProductDetails: Product not found with ID: " + productId);
                response.sendRedirect(request.getContextPath() + "/products");
            }
        } catch (NumberFormatException e) {
            System.err.println(
                    "[ViVuStoreApplication] handleProductDetails: Invalid product ID format: " + productIdParam);
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }

    /**
     * Handle adding a new product
     */
    private void handleAddProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Extract product details from form submission
            String name = request.getParameter("name");
            double unitPrice = Double.parseDouble(request.getParameter("unitPrice"));
            int unitInStock = Integer.parseInt(request.getParameter("unitInStock"));
            String thumbnailUrl = request.getParameter("thumbnailUrl");

            // Validate required fields
            if (name == null || name.trim().isEmpty() || thumbnailUrl == null || thumbnailUrl.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Product name and thumbnail URL are required");
                forwardToPage(request, response, "/product-form.jsp");
                return;
            }

            // Create and save new product
            Product newProduct = new Product(0, name, unitPrice, unitInStock, thumbnailUrl);
            Product savedProduct = productService.saveProduct(newProduct);

            System.out.println("[ViVuStoreApplication] handleAddProduct: Product added successfully with ID: "
                    + savedProduct.getId());

            // Redirect to products page
            response.sendRedirect(request.getContextPath() + "/products");

        } catch (NumberFormatException e) {
            System.err.println("[ViVuStoreApplication] handleAddProduct: Invalid number format in form submission");
            e.printStackTrace(System.err);

            request.setAttribute("errorMessage", "Invalid price or stock quantity format");
            forwardToPage(request, response, "/product-form.jsp");
        } catch (Exception e) {
            System.err.println("[ViVuStoreApplication] handleAddProduct: Error adding product");
            e.printStackTrace(System.err);

            request.setAttribute("errorMessage", "Error adding product. Please try again.");
            forwardToPage(request, response, "/product-form.jsp");
        }
    }

    /**
     * Handle updating an existing product
     */
    private void handleUpdateProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Extract product details from form submission
            int id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            double unitPrice = Double.parseDouble(request.getParameter("unitPrice"));
            int unitInStock = Integer.parseInt(request.getParameter("unitInStock"));
            String thumbnailUrl = request.getParameter("thumbnailUrl");

            // Validate required fields
            if (name == null || name.trim().isEmpty() || thumbnailUrl == null || thumbnailUrl.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Product name and thumbnail URL are required");
                request.setAttribute("product", productService.findProductById(id).orElse(new Product()));
                forwardToPage(request, response, "/product-form.jsp");
                return;
            }

            // Check if product exists
            if (!productService.productExistsById(id)) {
                System.err.println("[ViVuStoreApplication] handleUpdateProduct: Product not found with ID: " + id);
                response.sendRedirect(request.getContextPath() + "/products");
                return;
            }

            // Update and save product
            Product updatedProduct = new Product(id, name, unitPrice, unitInStock, thumbnailUrl);
            productService.saveProduct(updatedProduct);

            System.out
                    .println("[ViVuStoreApplication] handleUpdateProduct: Product updated successfully with ID: " + id);

            // Redirect to products page
            response.sendRedirect(request.getContextPath() + "/products");

        } catch (NumberFormatException e) {
            System.err.println("[ViVuStoreApplication] handleUpdateProduct: Invalid number format in form submission");
            e.printStackTrace(System.err);

            request.setAttribute("errorMessage", "Invalid ID, price, or stock quantity format");
            forwardToPage(request, response, "/product-form.jsp");
        } catch (Exception e) {
            System.err.println("[ViVuStoreApplication] handleUpdateProduct: Error updating product");
            e.printStackTrace(System.err);

            request.setAttribute("errorMessage", "Error updating product. Please try again.");
            forwardToPage(request, response, "/product-form.jsp");
        }
    }

    /**
     * Handle deleting a product
     */
    private void handleDeleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int id = Integer.parseInt(request.getParameter("id"));

            productService.deleteProductById(id);
            System.out
                    .println("[ViVuStoreApplication] handleDeleteProduct: Product deleted successfully with ID: " + id);

            // Redirect back to products page
            response.sendRedirect(request.getContextPath() + "/products");

        } catch (NumberFormatException e) {
            System.err.println("[ViVuStoreApplication] handleDeleteProduct: Invalid product ID format");
            e.printStackTrace(System.err);
            response.sendRedirect(request.getContextPath() + "/products");
        } catch (Exception e) {
            System.err.println("[ViVuStoreApplication] handleDeleteProduct: Error deleting product");
            e.printStackTrace(System.err);
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }

    /**
     * Helper method to forward to a JSP page
     */
    private void forwardToPage(HttpServletRequest request, HttpServletResponse response, String jspPath)
            throws ServletException, IOException {

        RequestDispatcher dispatcher = request.getRequestDispatcher(jspPath);
        try {
            dispatcher.forward(request, response);
            System.out.println("[ViVuStoreApplication] forwardToPage: Forward completed successfully to " + jspPath);
        } catch (Exception e) {
            System.err.println("[ViVuStoreApplication] forwardToPage: Exception during forward to " + jspPath);
            e.printStackTrace(System.err);
            throw e;
        }
    }

    /**
     * Handle displaying the product edit form
     */
    private void handleEditProductForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productIdParam = request.getParameter("id");

        if (productIdParam == null || productIdParam.isEmpty()) {
            // Redirect to products page if no ID provided
            response.sendRedirect(request.getContextPath() + "/products");
            return;
        }

        try {
            int productId = Integer.parseInt(productIdParam);
            Optional<Product> productOpt = productService.findProductById(productId);

            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                request.setAttribute("product", product);
                request.setAttribute("title", "ViVu Store - Edit " + product.getName());

                forwardToPage(request, response, "/product-form.jsp");
            } else {
                // Product not found, redirect to products page
                System.out.println(
                        "[ViVuStoreApplication] handleEditProductForm: Product not found with ID: " + productId);
                response.sendRedirect(request.getContextPath() + "/products");
            }
        } catch (NumberFormatException e) {
            System.err.println(
                    "[ViVuStoreApplication] handleEditProductForm: Invalid product ID format: " + productIdParam);
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }
}