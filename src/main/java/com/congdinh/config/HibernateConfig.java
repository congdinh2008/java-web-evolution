package com.congdinh.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * Hibernate configuration for the application using JPA with Hibernate as provider
 */
@org.springframework.context.annotation.Configuration
@EnableTransactionManagement
@PropertySource("classpath:db.properties")
public class HibernateConfig {
    
    private final DataSource dataSource;
    
    @Autowired
    public HibernateConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * EntityManagerFactory bean (JPA approach for Hibernate 6)
     */
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.congdinh.models");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        emf.setJpaVendorAdapter(vendorAdapter);
        
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        jpaProperties.put("hibernate.show_sql", "true");
        jpaProperties.put("hibernate.format_sql", "true");
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        jpaProperties.put("hibernate.current_session_context_class", "thread");
        emf.setJpaProperties(jpaProperties);
        
        return emf;
    }
    
    /**
     * SessionFactory bean directly obtained from Hibernate native API
     * This approach uses both JPA and direct Hibernate SessionFactory
     */
    @Bean(name = "sessionFactory") 
    public SessionFactory sessionFactory(EntityManagerFactory emf) {
        return emf.unwrap(SessionFactory.class);
    }
    
    /**
     * Transaction manager for JPA
     */
    @Bean
    public PlatformTransactionManager transactionManager(@org.springframework.beans.factory.annotation.Qualifier("entityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}