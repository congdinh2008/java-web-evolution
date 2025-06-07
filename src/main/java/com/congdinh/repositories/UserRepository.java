package com.congdinh.repositories;

import com.congdinh.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for User entity
 * Provides all the standard CRUD operations from JpaRepository plus custom queries
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by phone number
     * @param phoneNumber Phone number to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    /**
     * Check if a user exists with the given phone number
     * @param phoneNumber Phone number to check
     * @return true if user exists, false otherwise
     */
    boolean existsByPhoneNumber(String phoneNumber);
    
    /**
     * Find users by first name containing (case insensitive)
     * @param firstName First name to search for
     * @return List of users matching the criteria
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))")
    java.util.List<User> findByFirstNameContainingIgnoreCase(@Param("firstName") String firstName);
    
    /**
     * Find users by last name containing (case insensitive)
     * @param lastName Last name to search for
     * @return List of users matching the criteria
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    java.util.List<User> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);
    
    /**
     * Find users by role name
     * @param roleName Role name to search for
     * @return List of users with the specified role
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    java.util.List<User> findByRoleName(@Param("roleName") String roleName);
}