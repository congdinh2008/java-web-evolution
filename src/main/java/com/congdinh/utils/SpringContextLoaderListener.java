package com.congdinh.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;

/**
 * Custom ContextLoaderListener to initialize and provide access to the Spring ApplicationContext
 */
public class SpringContextLoaderListener extends ContextLoaderListener {
    
    private static ApplicationContext context;
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Call the parent method to initialize the WebApplicationContext
        super.contextInitialized(event);
        
        // Store the ApplicationContext in our static variable
        ServletContext servletContext = event.getServletContext();
        context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        
        System.out.println("[SpringContextLoaderListener] Spring ApplicationContext initialized successfully");
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
