package com.congdinh.services;

import com.congdinh.dto.UserDTO;
import com.congdinh.dto.mapper.UserMapper;
import com.congdinh.models.Role;
import com.congdinh.models.User;
import com.congdinh.repositories.RoleRepository;
import com.congdinh.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service layer for User-related operations
 * Implements UserDetailsService for Spring Security integration
 */
@Service
@Transactional
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // UserDetailsService implementation for Spring Security
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + username));
    }
    
    // CRUD Operations
    
    /**
     * Find all users
     * @return List of all users as DTOs
     */
    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toDTOList(users);
    }
    
    /**
     * Find user by ID
     * @param id User ID
     * @return Optional containing the user DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> findUserById(Integer id) {
        Optional<User> userOpt = userRepository.findById(id);
        return userOpt.map(UserMapper::toDTO);
    }
    
    /**
     * Find user by phone number
     * @param phoneNumber Phone number
     * @return Optional containing the user DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> findUserByPhoneNumber(String phoneNumber) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        return userOpt.map(UserMapper::toDTO);
    }
    
    /**
     * Find user by email
     * @param email Email address
     * @return Optional containing the user DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> findUserByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.map(UserMapper::toDTO);
    }
    
    /**
     * Save a user (register or update)
     * @param userDTO User DTO to save
     * @return Saved user DTO with ID
     */
    public UserDTO saveUser(UserDTO userDTO) {
        User user;
        
        if (userDTO.getId() != null) {
            // Update existing user
            user = userRepository.findById(userDTO.getId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userDTO.getId()));
            UserMapper.updateEntityFromDTO(user, userDTO);
        } else {
            // Create new user
            user = UserMapper.toEntity(userDTO);
            if (userDTO.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
        }
        
        User savedUser = userRepository.save(user);
        return UserMapper.toDTO(savedUser);
    }
    
    /**
     * Register a new user with default role
     * @param userDTO User DTO
     * @param defaultRoleName Default role name to assign
     * @return Saved user DTO
     */
    public UserDTO registerUser(UserDTO userDTO, String defaultRoleName) {
        // Encode password
        User user = UserMapper.toEntityWithPassword(userDTO, passwordEncoder.encode(userDTO.getPassword()));
        
        // Assign default role
        Optional<Role> defaultRole = roleRepository.findByName(defaultRoleName);
        if (defaultRole.isPresent()) {
            user.addRole(defaultRole.get());
        }
        
        User savedUser = userRepository.save(user);
        return UserMapper.toDTO(savedUser);
    }
    
    /**
     * Update user password
     * @param userId User ID
     * @param newPassword New password (plain text)
     */
    public void updateUserPassword(Integer userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * Delete a user by ID
     * @param id User ID to delete
     */
    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }
    
    // User Role Management
    
    /**
     * Add role to user
     * @param userId User ID
     * @param roleId Role ID
     */
    public void addRoleToUser(Integer userId, Integer roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
        
        user.addRole(role);
        userRepository.save(user);
    }
    
    /**
     * Remove role from user
     * @param userId User ID
     * @param roleId Role ID
     */
    public void removeRoleFromUser(Integer userId, Integer roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
        
        user.removeRole(role);
        userRepository.save(user);
    }
    
    /**
     * Update user roles
     * @param userId User ID
     * @param roleIds Set of role IDs to assign
     */
    public void updateUserRoles(Integer userId, Set<Integer> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Clear existing roles
        user.getRoles().clear();
        
        // Add new roles
        for (Integer roleId : roleIds) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
            user.addRole(role);
        }
        
        userRepository.save(user);
    }
    
    // Authentication & Security
    
    /**
     * Update last login time
     * @param phoneNumber Phone number
     */
    public void updateLastLogin(String phoneNumber) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.updateLastLogin();
            userRepository.save(user);
        }
    }
    
    /**
     * Enable/disable user account
     * @param userId User ID
     * @param enabled Whether to enable or disable
     */
    public void setUserEnabled(Integer userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        user.setEnabled(enabled);
        userRepository.save(user);
    }
    
    /**
     * Lock/unlock user account
     * @param userId User ID
     * @param locked Whether to lock or unlock
     */
    public void setUserLocked(Integer userId, boolean locked) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        user.setAccountNonLocked(!locked);
        userRepository.save(user);
    }
    
    // Query Operations
    
    /**
     * Find paginated users
     * @param page Page number
     * @param size Page size
     * @param sortBy Sort field
     * @param direction Sort direction
     * @return Page of user DTOs
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> findPaginatedUsers(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(UserMapper::toDTO);
    }
    
    /**
     * Search users by term
     * @param searchTerm Search term
     * @param page Page number
     * @param size Page size
     * @param sortBy Sort field
     * @param direction Sort direction
     * @return Page of user DTOs matching search
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsers(String searchTerm, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<User> userPage = userRepository.searchUsers(searchTerm, pageable);
        return userPage.map(UserMapper::toDTO);
    }
    
    /**
     * Find users by role
     * @param roleName Role name
     * @return List of user DTOs with the specified role
     */
    @Transactional(readOnly = true)
    public List<UserDTO> findUsersByRole(String roleName) {
        List<User> users = userRepository.findByRoleName(roleName);
        return UserMapper.toDTOList(users);
    }
    
    // Validation Operations
    
    /**
     * Check if a user exists by ID
     * @param id User ID
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean userExistsById(Integer id) {
        return userRepository.existsById(id);
    }
    
    /**
     * Check if phone number exists
     * @param phoneNumber Phone number to check
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean phoneNumberExists(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
    
    /**
     * Check if email exists
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Check if phone number exists excluding a specific user
     * @param phoneNumber Phone number to check
     * @param userId User ID to exclude
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean phoneNumberExistsExcludingUser(String phoneNumber, Integer userId) {
        return userRepository.existsByPhoneNumberAndIdNot(phoneNumber, userId);
    }
    
    /**
     * Check if email exists excluding a specific user
     * @param email Email to check
     * @param userId User ID to exclude
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean emailExistsExcludingUser(String email, Integer userId) {
        return userRepository.existsByEmailAndIdNot(email, userId);
    }
    
    /**
     * Check if user exists by email
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Check if user exists by phone number
     * @param phoneNumber Phone number to check
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
    
    /**
     * Create a new user with default USER role
     * @param userDTO User data
     * @return Created user
     */
    @Transactional
    public User createUser(UserDTO userDTO) {
        User user = UserMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        
        // Add default USER role
        Optional<Role> userRole = roleRepository.findByName("USER");
        if (userRole.isPresent()) {
            user.addRole(userRole.get());
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Create a new user with specified roles
     * @param userDTO User data
     * @param roleIds List of role IDs
     * @return Created user
     */
    @Transactional
    public User createUserWithRoles(UserDTO userDTO, List<Long> roleIds) {
        User user = UserMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        
        if (roleIds != null && !roleIds.isEmpty()) {
            List<Integer> intRoleIds = roleIds.stream().map(Long::intValue).collect(Collectors.toList());
            List<Role> roles = roleRepository.findAllById(intRoleIds);
            roles.forEach(user::addRole);
        } else {
            // Add default USER role if no roles specified
            Optional<Role> userRole = roleRepository.findByName("USER");
            if (userRole.isPresent()) {
                user.addRole(userRole.get());
            }
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Update user with roles
     * @param userDTO User data
     * @param roleIds List of role IDs
     * @return Updated user
     */
    @Transactional
    public User updateUserWithRoles(UserDTO userDTO, List<Long> roleIds) {
        Optional<User> existingUserOpt = userRepository.findById(userDTO.getId().intValue());
        if (existingUserOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        User existingUser = existingUserOpt.get();
        
        // Update user fields
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setEnabled(userDTO.isEnabled());
        
        // Update password if provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        // Update roles
        existingUser.getRoles().clear();
        if (roleIds != null && !roleIds.isEmpty()) {
            List<Integer> intRoleIds = roleIds.stream().map(Long::intValue).collect(Collectors.toList());
            List<Role> roles = roleRepository.findAllById(intRoleIds);
            roles.forEach(existingUser::addRole);
        }
        
        return userRepository.save(existingUser);
    }
    
    /**
     * Get user by ID
     * @param id User ID
     * @return Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id.intValue());
    }
    
    /**
     * Get all users with pagination
     * @param pageable Pagination information
     * @return Page of users
     */
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    /**
     * Search users by keyword
     * @param keyword Search keyword
     * @param pageable Pagination information
     * @return Page of users matching the search
     */
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userRepository.searchUsers(keyword, pageable);
    }
    
    /**
     * Get total number of users
     * @return Total user count
     */
    @Transactional(readOnly = true)
    public long getTotalUsers() {
        return userRepository.count();
    }
    
    /**
     * Get number of active users
     * @return Active user count
     */
    @Transactional(readOnly = true)
    public long getActiveUsers() {
        return userRepository.findByEnabledTrue().size();
    }
    
    /**
     * Get number of inactive users
     * @return Inactive user count
     */
    @Transactional(readOnly = true)
    public long getInactiveUsers() {
        return userRepository.findByEnabledFalse().size();
    }
    
    /**
     * Get number of users with ADMIN role
     * @return Admin user count
     */
    @Transactional(readOnly = true)
    public long getAdminUsers() {
        return userRepository.countByRoleName("ADMIN");
    }
    
    /**
     * Toggle user enabled status
     * @param id User ID
     * @return Updated user
     */
    @Transactional
    public User toggleUserStatus(Long id) {
        Optional<User> userOpt = userRepository.findById(id.intValue());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        User user = userOpt.get();
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }
    
    /**
     * Reset user password
     * @param id User ID
     * @param newPassword New password
     */
    @Transactional
    public void resetUserPassword(Long id, String newPassword) {
        Optional<User> userOpt = userRepository.findById(id.intValue());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * Disable user account
     * @param id User ID
     */
    @Transactional
    public void disableUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id.intValue());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        User user = userOpt.get();
        user.setEnabled(false);
        userRepository.save(user);
    }
}
