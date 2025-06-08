package com.congdinh.repositories;

import com.congdinh.models.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Role entity
 * Provides all the standard CRUD operations from JpaRepository plus custom queries
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    /**
     * Find role by name
     * @param name Role name
     * @return Optional containing the role if found
     */
    Optional<Role> findByName(String name);
    
    /**
     * Check if role name exists
     * @param name Role name to check
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);
    
    /**
     * Check if role name exists excluding a specific role
     * @param name Role name to check
     * @param id Role ID to exclude
     * @return true if exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r WHERE r.name = :name AND r.id != :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Integer id);
    
    /**
     * Search roles by name or description
     * @param searchTerm Search term
     * @param pageable Pagination information
     * @return Page of roles matching the search criteria
     */
    @Query("SELECT r FROM Role r WHERE " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Role> searchRoles(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Find roles with users
     * @return List of roles that have at least one user
     */
    @Query("SELECT DISTINCT r FROM Role r WHERE r.users IS NOT EMPTY")
    List<Role> findRolesWithUsers();
    
    /**
     * Find roles without users
     * @return List of roles that have no users
     */
    @Query("SELECT r FROM Role r WHERE r.users IS EMPTY")
    List<Role> findRolesWithoutUsers();
    
    /**
     * Count users in a role
     * @param roleId Role ID
     * @return Number of users in the role
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = :roleId")
    long countUsersInRole(@Param("roleId") Integer roleId);
    
    /**
     * Find default user roles (commonly assigned roles)
     * @return List of default roles
     */
    @Query("SELECT r FROM Role r WHERE r.name IN ('USER', 'CUSTOMER', 'MEMBER')")
    List<Role> findDefaultUserRoles();
    
    /**
     * Find admin roles
     * @return List of admin roles
     */
    @Query("SELECT r FROM Role r WHERE r.name IN ('ADMIN', 'SUPER_ADMIN', 'MODERATOR')")
    List<Role> findAdminRoles();
    
    /**
     * Find users by role name with pagination
     * @param roleName Role name
     * @param pageable Pagination information
     * @return Page of users with the specified role
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    Page<com.congdinh.models.User> findUsersByRole(@Param("roleName") String roleName, Pageable pageable);
}
