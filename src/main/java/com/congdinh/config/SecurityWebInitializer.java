package com.congdinh.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * Security Web Application Initializer
 * This class ensures that Spring Security filter chain is registered
 */
public class SecurityWebInitializer extends AbstractSecurityWebApplicationInitializer {
    // No additional configuration needed - Spring Security filters will be automatically registered
}