package com.congdinh.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Generic Repository interface defining standard CRUD operations
 * @param <T> the entity type
 * @param <ID> the primary key type
 */
public interface Repository<T, ID> {
    
    /**
     * Find all entities
     * @return list of all entities
     */
    List<T> findAll();
    
    /**
     * Find entity by its ID
     * @param id the entity ID
     * @return an Optional containing the entity if found, empty otherwise
     */
    Optional<T> findById(ID id);
    
    /**
     * Save a new entity or update an existing one
     * @param entity the entity to save
     * @return the saved entity with updated information (like generated ID)
     */
    T save(T entity);
    
    /**
     * Delete an entity by its ID
     * @param id the entity ID to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteById(ID id);
    
    /**
     * Check if an entity with the given ID exists
     * @param id the entity ID to check
     * @return true if entity exists, false otherwise
     */
    boolean existsById(ID id);
}
