package com.congdinh.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Spring Java Configuration class to replace XML configuration
 */
@Configuration
@ComponentScan(basePackages = "com.congdinh")
@PropertySource("classpath:db.properties")
public class AppConfig {
    
    @Autowired
    private Environment env;
    
    /**
     * Define DataSource bean
     * @return configured DataSource
     */
    @Bean
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.user"));
        dataSource.setPassword(env.getProperty("db.password"));
        return dataSource;
    }
    
    /**
     * Define JdbcTemplate bean
     * @return configured JdbcTemplate
     */
    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}
