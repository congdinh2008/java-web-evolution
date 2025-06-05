package com.congdinh.repositories;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.congdinh.models.Product;

/**
 * Repository implementation for Product entity using Hibernate
 */
@Repository
@Transactional
public class HibernateProductRepository implements ProductRepositoryInterface {
    
    private final SessionFactory sessionFactory;
    
    @Autowired
    public HibernateProductRepository(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    /**
     * Find all products
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        Session session = sessionFactory.openSession();
        try {
            Query<Product> query = session.createQuery("from Product", Product.class);
            return query.getResultList();
        } finally {
            session.close();
        }
    }
    
    /**
     * Find product by ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Integer id) {
        Session session = sessionFactory.openSession();
        try {
            Product product = session.get(Product.class, id);
            return Optional.ofNullable(product);
        } finally {
            session.close();
        }
    }
    
    /**
     * Save a product (insert or update)
     */
    @Override
    public Product save(Product product) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            // Replace deprecated saveOrUpdate with merge for Hibernate 6
            session.merge(product);
            session.getTransaction().commit();
            return product;
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
    
    /**
     * Delete a product by ID
     */
    @Override
    public boolean deleteById(Integer id) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            // Use jakarta.persistence.Query for mutation operations
            var query = session.createMutationQuery("delete from Product where id = :id");
            query.setParameter("id", id);
            int result = query.executeUpdate();
            session.getTransaction().commit();
            return result > 0;
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
    
    /**
     * Check if a product exists by ID
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> query = session.createQuery(
                    "select count(*) from Product where id = :id", Long.class);
            query.setParameter("id", id);
            return query.getSingleResult() > 0;
        } finally {
            session.close();
        }
    }
    
    /**
     * Create Products table if it doesn't exist
     * Note: With Hibernate's "update" setting for hbm2ddl.auto, this is handled automatically
     */
    public void createProductsTableIfNotExists() {
        // No need to manually create tables with Hibernate
        System.out.println("[HibernateProductRepository] createProductsTableIfNotExists: Using Hibernate schema generation");
    }
    
    /**
     * Initialize sample data if the table is empty
     */
    public void initializeSampleData() {
        Session session = sessionFactory.openSession();
        try {
            // Check if we have any products
            Query<Long> query = session.createQuery("select count(*) from Product", Long.class);
            long count = query.getSingleResult();
            
            if (count == 0) {
                System.out.println("[HibernateProductRepository] initializeSampleData: Adding sample data");
                
                // Add some sample products
                Product[] sampleProducts = {
                    new Product(0, "Smartphone X", 799.99, 50, "https://placehold.co/600x400?text=Smartphone+X"),
                    new Product(0, "Laptop Pro", 1299.99, 25, "https://placehold.co/600x400?text=Laptop+Pro"),
                    new Product(0, "Wireless Earbuds", 129.99, 100, "https://placehold.co/600x400?text=Wireless+Earbuds"),
                    new Product(0, "Smart Watch", 249.99, 30, "https://placehold.co/600x400?text=Smart+Watch"),
                    new Product(0, "4K TV", 899.99, 15, "https://placehold.co/600x400?text=4K+TV")
                };
                
                // Insert the products one by one with explicit transactions
                for (Product product : sampleProducts) {
                    save(product);
                }
                
                System.out.println("[HibernateProductRepository] initializeSampleData: Added " + sampleProducts.length + " sample products");
            } else {
                System.out.println("[HibernateProductRepository] initializeSampleData: Table already has data, skipping");
            }
        } finally {
            session.close();
        }
    }
}
