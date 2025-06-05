package com.congdinh.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.congdinh.models.Product;
import com.congdinh.utils.DatabaseUtil;

/**
 * Repository implementation for Product entity
 */
@Repository
public class ProductRepository implements com.congdinh.repositories.Repository<Product, Integer> {
    
    private static final String FIND_ALL_SQL = "SELECT Id, Name, UnitPrice, UnitInStock, ThumbnailUrl FROM Products";
    private static final String FIND_BY_ID_SQL = "SELECT Id, Name, UnitPrice, UnitInStock, ThumbnailUrl FROM Products WHERE Id = ?";
    private static final String SAVE_SQL = "INSERT INTO Products (Name, UnitPrice, UnitInStock, ThumbnailUrl) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Products SET Name = ?, UnitPrice = ?, UnitInStock = ?, ThumbnailUrl = ? WHERE Id = ?";
    private static final String DELETE_SQL = "DELETE FROM Products WHERE Id = ?";
    private static final String EXISTS_SQL = "SELECT COUNT(*) FROM Products WHERE Id = ?";
    private static final String COUNT_ALL_SQL = "SELECT COUNT(*) FROM Products";
    
    private JdbcTemplate jdbcTemplate;
    
    // Default constructor for non-Spring contexts
    public ProductRepository() {
        // Empty constructor
    }
    
    // Constructor with dependency injection
    @Autowired
    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // Getter and setter for jdbcTemplate (for backward compatibility)
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    // Row mapper for Product entity
    private RowMapper<Product> productRowMapper = (rs, rowNum) -> new Product(
        rs.getInt("Id"),
        rs.getString("Name"),
        rs.getDouble("UnitPrice"),
        rs.getInt("UnitInStock"),
        rs.getString("ThumbnailUrl")
    );

    /**
     * Find all products
     */
    @Override
    public List<Product> findAll() {
        if (jdbcTemplate != null) {
            // Use Spring JdbcTemplate
            return jdbcTemplate.query(FIND_ALL_SQL, productRowMapper);
        } else {
            // Fallback to direct JDBC
            List<Product> products = new ArrayList<>();
            
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SQL);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            } catch (SQLException e) {
                System.err.println("[ProductRepository] findAll: SQL Exception");
                e.printStackTrace(System.err);
            }
            
            return products;
        }
    }

    /**
     * Find product by ID
     */
    @Override
    public Optional<Product> findById(Integer id) {
        if (jdbcTemplate != null) {
            // Use Spring JdbcTemplate
            try {
                Product product = jdbcTemplate.queryForObject(FIND_BY_ID_SQL, productRowMapper, id);
                return Optional.ofNullable(product);
            } catch (Exception e) {
                System.err.println("[ProductRepository] findById: Exception for ID: " + id);
                e.printStackTrace(System.err);
                return Optional.empty();
            }
        } else {
            // Fallback to direct JDBC
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {
                
                stmt.setInt(1, id);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSetToProduct(rs));
                    }
                }
            } catch (SQLException e) {
                System.err.println("[ProductRepository] findById: SQL Exception for ID: " + id);
                e.printStackTrace(System.err);
            }
            
            return Optional.empty();
        }
    }

    /**
     * Save a product (insert or update)
     */
    @Override
    public Product save(Product product) {
        if (jdbcTemplate != null) {
            return saveWithJdbcTemplate(product);
        } else {
            return saveWithDirectJdbc(product);
        }
    }
    
    /**
     * Save using Spring JdbcTemplate
     */
    private Product saveWithJdbcTemplate(Product product) {
        if (product.getId() > 0 && existsById(product.getId())) {
            // Update existing product
            jdbcTemplate.update(UPDATE_SQL, 
                product.getName(), 
                product.getUnitPrice(), 
                product.getUnitInStock(), 
                product.getThumbnailUrl(),
                product.getId()
            );
            return product;
        } else {
            // Insert new product
            KeyHolder keyHolder = new GeneratedKeyHolder();
            
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, product.getName());
                ps.setDouble(2, product.getUnitPrice());
                ps.setInt(3, product.getUnitInStock());
                ps.setString(4, product.getThumbnailUrl());
                return ps;
            }, keyHolder);
            
            Number key = keyHolder.getKey();
            if (key != null) {
                product.setId(key.intValue());
            }
            
            return product;
        }
    }
    
    /**
     * Save using direct JDBC connection
     */
    private Product saveWithDirectJdbc(Product product) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            
            if (product.getId() > 0 && existsById(product.getId())) {
                // Update existing product
                stmt = conn.prepareStatement(UPDATE_SQL);
                stmt.setString(1, product.getName());
                stmt.setDouble(2, product.getUnitPrice());
                stmt.setInt(3, product.getUnitInStock());
                stmt.setString(4, product.getThumbnailUrl());
                stmt.setInt(5, product.getId());
                
                stmt.executeUpdate();
            } else {
                // Insert new product
                stmt = conn.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, product.getName());
                stmt.setDouble(2, product.getUnitPrice());
                stmt.setInt(3, product.getUnitInStock());
                stmt.setString(4, product.getThumbnailUrl());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows > 0) {
                    rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        product.setId(rs.getInt(1));
                    }
                }
            }
            
            return product;
        } catch (SQLException e) {
            System.err.println("[ProductRepository] save: SQL Exception");
            e.printStackTrace(System.err);
            return product; // Return original product on error
        } finally {
            DatabaseUtil.closeResources(rs, stmt, conn);
        }
    }

    /**
     * Delete a product by ID
     */
    @Override
    public boolean deleteById(Integer id) {
        if (jdbcTemplate != null) {
            // Use Spring JdbcTemplate
            int rowsAffected = jdbcTemplate.update(DELETE_SQL, id);
            return rowsAffected > 0;
        } else {
            // Fallback to direct JDBC
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
                
                stmt.setInt(1, id);
                
                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
            } catch (SQLException e) {
                System.err.println("[ProductRepository] deleteById: SQL Exception for ID: " + id);
                e.printStackTrace(System.err);
                return false;
            }
        }
    }

    /**
     * Check if a product exists by ID
     */
    @Override
    public boolean existsById(Integer id) {
        if (jdbcTemplate != null) {
            // Use Spring JdbcTemplate
            try {
                Integer count = jdbcTemplate.queryForObject(EXISTS_SQL, Integer.class, id);
                return count != null && count > 0;
            } catch (Exception e) {
                System.err.println("[ProductRepository] existsById: Exception for ID: " + id);
                e.printStackTrace(System.err);
                return false;
            }
        } else {
            // Fallback to direct JDBC
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(EXISTS_SQL)) {
                
                stmt.setInt(1, id);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            } catch (SQLException e) {
                System.err.println("[ProductRepository] existsById: SQL Exception for ID: " + id);
                e.printStackTrace(System.err);
            }
            
            return false;
        }
    }
    
    /**
     * Check if the products table is empty
     * @return true if no products exist, false otherwise
     */
    public boolean isEmpty() {
        if (jdbcTemplate != null) {
            // Use Spring JdbcTemplate
            try {
                Integer count = jdbcTemplate.queryForObject(COUNT_ALL_SQL, Integer.class);
                return count == null || count == 0;
            } catch (Exception e) {
                System.err.println("[ProductRepository] isEmpty: Exception");
                e.printStackTrace(System.err);
                return true; // Default to true (empty) on error
            }
        } else {
            // Fallback to direct JDBC
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(COUNT_ALL_SQL);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            } catch (SQLException e) {
                System.err.println("[ProductRepository] isEmpty: SQL Exception");
                e.printStackTrace(System.err);
            }
            
            // Default to true (empty) on error or if no result
            return true;
        }
    }
    
    /**
     * Initialize the Products table if it doesn't exist
     */
    public void createProductsTableIfNotExists() {
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

        if (jdbcTemplate != null) {
            // Use Spring JdbcTemplate
            jdbcTemplate.execute(createTableSQL);
            System.out.println("[ProductRepository] createProductsTableIfNotExists: Table checked/created successfully");
        } else {
            // Fallback to direct JDBC
            try (Connection conn = DatabaseUtil.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                stmt.execute(createTableSQL);
                System.out.println("[ProductRepository] createProductsTableIfNotExists: Table checked/created successfully");
            } catch (SQLException e) {
                System.err.println("[ProductRepository] createProductsTableIfNotExists: SQL Exception");
                e.printStackTrace(System.err);
            }
        }
    }
    
    /**
     * Initialize sample data if the table is empty
     */
    public void initializeSampleData() {
        if (!isEmpty()) {
            System.out.println("[ProductRepository] initializeSampleData: Products table already has data");
            return;
        }
        
        List<Product> sampleProducts = new ArrayList<>();
        sampleProducts.add(new Product(0, "Laptop Pro X", 1299.99, 15, "https://placehold.co/600x400?text=Laptop+Pro+X"));
        sampleProducts.add(new Product(0, "Smartphone Ultra", 799.99, 25, "https://placehold.co/600x400?text=Smartphone+Ultra"));
        sampleProducts.add(new Product(0, "Wireless Headphones", 199.99, 50, "https://placehold.co/600x400?text=Wireless+Headphones"));
        sampleProducts.add(new Product(0, "4K Smart TV", 899.99, 10, "https://placehold.co/600x400?text=4K+Smart+TV"));
        sampleProducts.add(new Product(0, "Digital Camera", 499.99, 20, "https://placehold.co/600x400?text=Digital+Camera"));
        sampleProducts.add(new Product(0, "Fitness Tracker", 129.99, 35, "https://placehold.co/600x400?text=Fitness+Tracker"));
        
        for (Product product : sampleProducts) {
            save(product);
        }
        
        System.out.println("[ProductRepository] initializeSampleData: Sample data inserted successfully");
    }
    
    /**
     * Map ResultSet to Product entity
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("Id"),
            rs.getString("Name"),
            rs.getDouble("UnitPrice"),
            rs.getInt("UnitInStock"),
            rs.getString("ThumbnailUrl")
        );
    }
}
