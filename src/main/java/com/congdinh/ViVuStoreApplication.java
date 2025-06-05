package com.congdinh;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.congdinh.models.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

public class ViVuStoreApplication extends HttpServlet {
    // private List<Product> productList; // We will fetch this from DB

    // --- Database Connection Details (Replace with your actual details) ---
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=javaWebEvolution_dev;encrypt=true;trustServerCertificate=true;";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "abcd@1234";
    // --- End Database Connection Details ---

    @Override
    public void init() throws ServletException {
        // JDBC drivers are typically auto-loaded (JDBC 4.0+).
        // Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver") is usually not
        // needed.
        // If you encounter issues, you might need to uncomment the Class.forName call
        // and ensure the driver JAR is correctly placed in your server's lib directory
        // or defined as a provided dependency if your server manages it.
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("[ViVuStoreApplication] init: SQL Server JDBC Driver loaded successfully via Class.forName().");
        } catch (ClassNotFoundException e) {
            System.err.println("[ViVuStoreApplication] init: ERROR - SQL Server JDBC Driver not found via Class.forName()!");
            e.printStackTrace(System.err);
            throw new ServletException("SQL Server JDBC Driver not found", e);
        }
        // Create the Products table if it does not exist
        try {
            createProductsTable();
        } catch (ServletException e) {
            System.err.println("[ViVuStoreApplication] init: Error creating Products table.");
            e.printStackTrace(System.err);
        }

        System.out.println("[ViVuStoreApplication] init: Products table checked/created.");
        
        // Optionally, initialize sample data if the table is empty
        try {
            initializeSampleData();
        } catch (ServletException e) {
            System.err.println("[ViVuStoreApplication] init: Error initializing sample data.");
            e.printStackTrace(System.err);
        }
    }

    private void createProductsTable() throws ServletException {
        String createTableSQL = 
            "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[Products]') AND type in (N'U')) " +
            "BEGIN " +
            "    CREATE TABLE [dbo].[Products] ( " +
            "        Id INT PRIMARY KEY IDENTITY(1,1), " +
            "        Name NVARCHAR(100) NOT NULL, " +
            "        UnitPrice DECIMAL(10, 2) NOT NULL, " +
            "        UnitInStock INT NOT NULL, " +
            "        ThumbnailUrl NVARCHAR(255) NOT NULL " +
            "    ); " +
            "END;";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(createTableSQL);
            System.out.println("[ViVuStoreApplication] createProductsTable: Products table checked/created successfully.");
        } catch (SQLException e) {
            System.err.println("[ViVuStoreApplication] createProductsTable: SQL EXCEPTION during table creation/check!");
            e.printStackTrace(System.err);
            throw new ServletException("Error creating/checking Products table.", e);
        }
    }

    private List<Product> getProductsFromDB() throws ServletException {
        List<Product> products = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            System.out.println("[ViVuStoreApplication] getProductsFromDB: Attempting to connect to database...");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("[ViVuStoreApplication] getProductsFromDB: Database connection successful.");

            // Debug: Check if the Products table exists
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "Products", null);
            if (tables.next()) {
                System.out.println("[ViVuStoreApplication] getProductsFromDB: Products table exists in the database.");
            } else {
                System.out.println("[ViVuStoreApplication] getProductsFromDB: WARNING - Products table does NOT exist in the database!");
                
                // Try to create the table again, just in case
                createProductsTable();
                
                // Check if we can find table with explicit schema
                tables = dbm.getTables(null, "dbo", "Products", null);
                if (tables.next()) {
                    System.out.println("[ViVuStoreApplication] getProductsFromDB: Products table exists in the dbo schema.");
                } else {
                    System.out.println("[ViVuStoreApplication] getProductsFromDB: WARNING - Products table does NOT exist in the dbo schema either!");
                }
            }

            // Use fully qualified table name in query
            String sql = "SELECT Id, Name, UnitPrice, UnitInStock, ThumbnailUrl FROM [dbo].[Products]";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            System.out.println("[ViVuStoreApplication] getProductsFromDB: Executed query. Processing results...");
            while (rs.next()) {
                int id = rs.getInt("Id");
                String name = rs.getString("Name");
                double unitPrice = rs.getDouble("UnitPrice");
                int unitInStock = rs.getInt("UnitInStock");
                String thumbnailUrl = rs.getString("ThumbnailUrl");
                products.add(new Product(id, name, unitPrice, unitInStock, thumbnailUrl));
            }
            System.out.println("[ViVuStoreApplication] getProductsFromDB: Found " + products.size() + " products.");

        } catch (SQLException e) {
            System.err.println("[ViVuStoreApplication] getProductsFromDB: SQL EXCEPTION!");
            e.printStackTrace(System.err);
            throw new ServletException("Error fetching products from database.", e);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
                System.out.println("[ViVuStoreApplication] getProductsFromDB: Database resources closed.");
            } catch (SQLException e) {
                System.err.println("[ViVuStoreApplication] getProductsFromDB: ERROR closing database resources!");
                e.printStackTrace(System.err);
            }
        }
        return products;
    }

    // Optional: Method to insert sample data if the table is empty or for testing
    private void initializeSampleData() throws ServletException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement stmt = conn.createStatement()) {

            // Check if table is empty
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM [dbo].[Products]");
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println(
                        "[ViVuStoreApplication] initializeSampleData: Products table is empty. Inserting sample data...");
                stmt.executeUpdate(
                        "INSERT INTO [dbo].[Products] (Name, UnitPrice, UnitInStock, ThumbnailUrl) VALUES ('Smartphone X', 799.99, 15, 'https://placehold.co/600x400/E8117F/white?text=Smartphone')");
                stmt.executeUpdate(
                        "INSERT INTO [dbo].[Products] (Name, UnitPrice, UnitInStock, ThumbnailUrl) VALUES ('Laptop Pro', 1299.99, 10, 'https://placehold.co/600x400/3498DB/white?text=Laptop')");
                stmt.executeUpdate(
                        "INSERT INTO [dbo].[Products] (Name, UnitPrice, UnitInStock, ThumbnailUrl) VALUES ('Wireless Headphones', 199.99, 25, 'https://placehold.co/600x400/2ECC71/white?text=Headphones')");
                stmt.executeUpdate(
                        "INSERT INTO [dbo].[Products] (Name, UnitPrice, UnitInStock, ThumbnailUrl) VALUES ('Smart Watch', 299.99, 20, 'https://placehold.co/600x400/F39C12/white?text=SmartWatch')");
                stmt.executeUpdate(
                        "INSERT INTO [dbo].[Products] (Name, UnitPrice, UnitInStock, ThumbnailUrl) VALUES ('4K Smart TV', 899.99, 5, 'https://placehold.co/600x400/9B59B6/white?text=Smart+TV')");
                System.out.println("[ViVuStoreApplication] initializeSampleData: Sample data inserted.");
            } else {
                System.out.println(
                        "[ViVuStoreApplication] initializeSampleData: Products table already contains data or an error occurred.");
            }
        } catch (SQLException e) {
            System.err.println(
                    "[ViVuStoreApplication] initializeSampleData: SQL EXCEPTION during sample data initialization!");
            e.printStackTrace(System.err);
            // Not throwing ServletException here as it's optional, but logging is important
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[ViVuStoreApplication] doGet: Entered method. Path: " + request.getServletPath()); // DEBUG

        String path = request.getServletPath();
        System.out.println("[ViVuStoreApplication] doGet: Path: " + path); // DEBUG

        // Set common attributes for all pages
        request.setAttribute("title", "ViVu Store - Home Page");

        String jspPath;
        switch (path) {
            case "/products":
                request.setAttribute("title", "ViVu Store - Our Products");
                try {
                    List<Product> productList = getProductsFromDB();
                    request.setAttribute("products", productList);
                } catch (ServletException e) {
                    System.err.println(
                            "[ViVuStoreApplication] doGet: Error getting products from DB for /products path.");
                    e.printStackTrace(System.err);
                    request.setAttribute("message", "Error: Could not load products.");
                    request.setAttribute("description", "Please try again later or contact support.");
                    jspPath = "/index.jsp";
                    request.setAttribute("products", new ArrayList<Product>());

                    // Forward to error display (e.g., index page with error message)
                    RequestDispatcher errorDispatcher = request.getRequestDispatcher(jspPath);
                    try {
                        errorDispatcher.forward(request, response);
                    } catch (ServletException | IOException forwardEx) {
                        System.err.println("[ViVuStoreApplication] doGet: EXCEPTION DURING ERROR FORWARD!");
                        forwardEx.printStackTrace(System.err);
                    }
                    return;
                }
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

        System.out.println("[ViVuStoreApplication] doGet: Forwarding to " + jspPath);

        RequestDispatcher dispatcher = request.getRequestDispatcher(jspPath);
        try {
            dispatcher.forward(request, response);
            System.out.println("[ViVuStoreApplication] doGet: Forward completed successfully");
        } catch (Exception e) { // Catching general Exception as specified by original code
            System.err.println("[ViVuStoreApplication] doGet: EXCEPTION DURING FORWARD!");
            e.printStackTrace(System.err);
            throw e;
        }
    }
}