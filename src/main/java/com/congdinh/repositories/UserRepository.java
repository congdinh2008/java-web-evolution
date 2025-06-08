package com.congdinh.repositories;

import com.congdinh.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for User entity
 * Provides all the standard CRUD operations from JpaRepository plus custom queries
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    /**
     * Find user by phone number (username)
     * @param phoneNumber Phone number
     * @return Optional containing the user if found
     */
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    /**
     * Find user by email
     * @param email Email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if phone number exists
     * @param phoneNumber Phone number to check
     * @return true if exists, false otherwise
     */
    boolean existsByPhoneNumber(String phoneNumber);
    
    /**
     * Check if email exists
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if phone number exists excluding a specific user
     * @param phoneNumber Phone number to check
     * @param userId User ID to exclude
     * @return true if exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.phoneNumber = :phoneNumber AND u.id != :userId")
    boolean existsByPhoneNumberAndIdNot(@Param("phoneNumber") String phoneNumber, @Param("userId") Integer userId);
    
    /**
     * Check if email exists excluding a specific user
     * @param email Email to check
     * @param userId User ID to exclude
     * @return true if exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.id != :userId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("userId") Integer userId);
    
    /**
     * Find users by role name
     * @param roleName Role name
     * @return List of users with the specified role
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
    
    /**
     * Find users by role name with pagination
     * @param roleName Role name
     * @param pageable Pagination information
     * @return Page of users with the specified role
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);
    
    /**
     * Search users by name or phone number
     * @param searchTerm Search term
     * @param pageable Pagination information
     * @return Page of users matching the search criteria
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "u.phoneNumber LIKE CONCAT('%', :searchTerm, '%') OR " +
           "(u.email IS NOT NULL AND LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Find users created between dates
     * @param startDate Start date
     * @param endDate End date
     * @return List of users created in the date range
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find enabled users
     * @return List of enabled users
     */
    List<User> findByEnabledTrue();
    
    /**
     * Find disabled users
     * @return List of disabled users
     */
    List<User> findByEnabledFalse();
    
    /**
     * Find users who logged in after a specific date
     * @param date Date threshold
     * @return List of users with recent login
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin > :date")
    List<User> findUsersWithRecentLogin(@Param("date") LocalDateTime date);
    
    /**
     * Count users by role
     * @param roleName Role name
     * @return Number of users with the specified role
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);
    
    /**
     * Find users without any roles
     * @return List of users with no roles assigned
     */
    @Query("SELECT u FROM User u WHERE u.roles IS EMPTY")
    List<User> findUsersWithoutRoles();
}
