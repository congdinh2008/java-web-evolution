package com.congdinh.services;

import com.congdinh.dto.UserDto;
import com.congdinh.dto.UserRegistrationDto;
import com.congdinh.dto.mapper.UserMapper;
import com.congdinh.models.Role;
import com.congdinh.models.User;
import com.congdinh.repositories.RoleRepository;
import com.congdinh.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for User-related operations
 */
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Find all users
     * @return List of all users as DTOs
     */
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toDtoList(users);
    }
    
    /**
     * Find user by ID
     * @param id User ID
     * @return Optional containing the user DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<UserDto> findUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        return userOpt.map(UserMapper::toDto);
    }
    
    /**
     * Find user by phone number
     * @param phoneNumber Phone number
     * @return Optional containing the user DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<UserDto> findUserByPhoneNumber(String phoneNumber) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        return userOpt.map(UserMapper::toDto);
    }
    
    /**
     * Register a new user
     * @param registrationDto User registration data
     * @return Saved user DTO with ID
     * @throws RuntimeException if phone number already exists or passwords don't match
     */
    public UserDto registerUser(UserRegistrationDto registrationDto) {
        // Validate passwords match
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        
        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(registrationDto.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }
        
        // Convert DTO to entity
        User user = UserMapper.toEntity(registrationDto);
        
        // Encode password
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        
        // Assign default user role
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default user role not found"));
        user.addRole(userRole);
        
        // Save user
        User savedUser = userRepository.save(user);
        return UserMapper.toDto(savedUser);
    }
    
    /**
     * Save a user (insert or update)
     * @param userDto User DTO to save
     * @return Saved user DTO with ID
     */
    public UserDto saveUser(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.toDto(savedUser);
    }
    
    /**
     * Update an existing user
     * @param id User ID to update
     * @param userDto User DTO with updated data
     * @return Updated user DTO
     * @throws RuntimeException if user not found
     */
    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        UserMapper.updateEntityFromDto(existingUser, userDto);
        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toDto(updatedUser);
    }
    
    /**
     * Delete a user by ID
     * @param id User ID to delete
     * @throws RuntimeException if user not found
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    /**
     * Check if a user exists with the given phone number
     * @param phoneNumber Phone number to check
     * @return true if user exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
    
    /**
     * Find users by role name
     * @param roleName Role name
     * @return List of users with the specified role
     */
    @Transactional(readOnly = true)
    public List<UserDto> findUsersByRoleName(String roleName) {
        List<User> users = userRepository.findByRoleName(roleName);
        return UserMapper.toDtoList(users);
    }
    
    /**
     * Add role to user
     * @param userId User ID
     * @param roleId Role ID
     * @throws RuntimeException if user or role not found
     */
    public void addRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        
        user.addRole(role);
        userRepository.save(user);
    }
    
    /**
     * Remove role from user
     * @param userId User ID
     * @param roleId Role ID
     * @throws RuntimeException if user or role not found
     */
    public void removeRoleFromUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        
        user.removeRole(role);
        userRepository.save(user);
    }
}