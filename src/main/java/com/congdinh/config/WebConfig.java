package com.congdinh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

/**
 * Spring MVC Configuration with Thymeleaf
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.congdinh.controllers"})
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * Configure Thymeleaf template resolver
     * @return SpringResourceTemplateResolver for resolving Thymeleaf templates
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false); // Set to true in production
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }
    
    /**
     * Configure Thymeleaf template engine
     * @return SpringTemplateEngine for processing templates
     */
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        
        // Add Layout Dialect for layouts and fragments
        templateEngine.addDialect(new LayoutDialect());
        
        return templateEngine;
    }
    
    /**
     * Configure Thymeleaf view resolver
     * @return ThymeleafViewResolver for resolving templates to views
     */
    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }
    
    /**
     * Configure static resource handling
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }
    
    /**
     * Configure static resource handling with default servlet
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
