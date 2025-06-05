package com.congdinh.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Initializer for Spring MVC DispatcherServlet
 * This class replaces the need for servlet configuration in web.xml
 */
public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        // AppConfig contains application-wide configurations like services and repositories
        return new Class<?>[] { AppConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        // WebConfig contains MVC-specific configurations
        return new Class<?>[] { WebConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        // Map the Spring DispatcherServlet to handle all requests
        return new String[] { "/" };
    }
}
