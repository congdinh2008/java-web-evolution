package com.congdinh.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application
 * Handles security exceptions, validation errors, and general exceptions
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler(AuthenticationException.class)
    public ModelAndView handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("error/403");
        modelAndView.addObject("error", "Authentication failed");
        modelAndView.addObject("message", "Invalid credentials. Please try again.");
        modelAndView.addObject("timestamp", LocalDateTime.now());
        modelAndView.addObject("path", request.getRequestURI());
        return modelAndView;
    }

    /**
     * Handle access denied exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("error/403");
        modelAndView.addObject("error", "Access Denied");
        modelAndView.addObject("message", "You don't have permission to access this resource.");
        modelAndView.addObject("timestamp", LocalDateTime.now());
        modelAndView.addObject("path", request.getRequestURI());
        return modelAndView;
    }

    /**
     * Handle bad credentials exceptions
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ModelAndView handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("error", "Invalid username or password");
        return modelAndView;
    }

    /**
     * Handle validation exceptions for API endpoints
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("errors", errors);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        ex.getConstraintViolations().forEach(violation -> 
            errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Constraint Violation");
        response.put("errors", errors);
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle 404 Not Found exceptions
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNotFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.addObject("error", "Page Not Found");
        modelAndView.addObject("message", "The requested page could not be found.");
        modelAndView.addObject("timestamp", LocalDateTime.now());
        modelAndView.addObject("path", request.getRequestURI());
        return modelAndView;
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", ex.getMessage());
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("error/500");
        modelAndView.addObject("error", "Internal Server Error");
        modelAndView.addObject("message", "An unexpected error occurred. Please try again later.");
        modelAndView.addObject("timestamp", LocalDateTime.now());
        modelAndView.addObject("path", request.getRequestURI());
        
        // Log the exception for debugging
        ex.printStackTrace();
        
        return modelAndView;
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("error/500");
        modelAndView.addObject("error", "Internal Server Error");
        modelAndView.addObject("message", "An unexpected error occurred. Please contact support if the problem persists.");
        modelAndView.addObject("timestamp", LocalDateTime.now());
        modelAndView.addObject("path", request.getRequestURI());
        
        // Log the exception for debugging
        ex.printStackTrace();
        
        return modelAndView;
    }
}
