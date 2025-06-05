package com.congdinh.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.congdinh.config.AppConfig;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;

/**
 * Custom ContextLoaderListener to initialize and provide access to the Spring ApplicationContext
 * Uses annotation-based configuration
 */
public class SpringContextLoaderListener extends ContextLoaderListener {
    
    private static ApplicationContext context;
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Set up the Annotation Config Web Application Context
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(AppConfig.class);
        
        // Set the ServletContext in the application context
        ServletContext servletContext = event.getServletContext();
        ctx.setServletContext(servletContext);
        
        // Initialize the WebApplicationContext
        ctx.refresh();
        
        // Store the ApplicationContext in our static variable
        context = ctx;
        
        System.out.println("[SpringContextLoaderListener] Spring ApplicationContext initialized successfully with Java-based configuration");
    }
    
    /**
     * Get the Spring ApplicationContext
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }
    
    /**
     * Get a bean from the Spring ApplicationContext
     * @param beanName Name of the bean
     * @return Bean instance
     */
    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }
    
    /**
     * Get a bean from the Spring ApplicationContext by type
     * @param beanClass Class of the bean
     * @return Bean instance
     */
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
    
    /**
     * Get a bean from the Spring ApplicationContext by name and type
     * @param beanName Name of the bean
     * @param beanClass Class of the bean
     * @return Bean instance
     */
    public static <T> T getBean(String beanName, Class<T> beanClass) {
        return context.getBean(beanName, beanClass);
    }
}
