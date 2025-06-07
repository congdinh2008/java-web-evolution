package com.congdinh.repositories;

import com.congdinh.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Role entity
 * Provides all the standard CRUD operations from JpaRepository plus custom queries
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find role by name
     * @param name Role name to search for
     * @return Optional containing the role if found
     */
    Optional<Role> findByName(String name);
    
    /**
     * Check if a role exists with the given name
     * @param name Role name to check
     * @return true if role exists, false otherwise
     */
    boolean existsByName(String name);
}