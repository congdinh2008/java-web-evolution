package com.congdinh.services;

import com.congdinh.dto.RoleDTO;
import com.congdinh.dto.mapper.RoleMapper;
import com.congdinh.models.Role;
import com.congdinh.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Role-related operations
 */
@Service
@Transactional
public class RoleService {
    
    private final RoleRepository roleRepository;
    
    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    // CRUD Operations
    
    /**
     * Find all roles
     * @return List of all roles as DTOs
     */
    @Transactional(readOnly = true)
    public List<RoleDTO> findAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return RoleMapper.toDTOList(roles);
    }
    
    /**
     * Find role by ID
     * @param id Role ID
     * @return Optional containing the role DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<RoleDTO> findRoleById(Integer id) {
        Optional<Role> roleOpt = roleRepository.findById(id);
        return roleOpt.map(RoleMapper::toDTO);
    }
    
    /**
     * Find role by name
     * @param name Role name
     * @return Optional containing the role DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<RoleDTO> findRoleByName(String name) {
        Optional<Role> roleOpt = roleRepository.findByName(name);
        return roleOpt.map(RoleMapper::toDTO);
    }
    
    /**
     * Save a role (insert or update)
     * @param roleDTO Role DTO to save
     * @return Saved role DTO with ID
     */
    public RoleDTO saveRole(RoleDTO roleDTO) {
        Role role;
        
        if (roleDTO.getId() != null) {
            // Update existing role
            role = roleRepository.findById(roleDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleDTO.getId()));
            RoleMapper.updateEntityFromDTO(role, roleDTO);
        } else {
            // Create new role
            role = RoleMapper.toEntity(roleDTO);
        }
        
        Role savedRole = roleRepository.save(role);
        return RoleMapper.toDTO(savedRole);
    }
    
    /**
     * Delete a role by ID
     * @param id Role ID to delete
     */
    public void deleteRoleById(Integer id) {
        // Check if role has users before deletion
        long userCount = roleRepository.countUsersInRole(id);
        if (userCount > 0) {
            throw new RuntimeException("Cannot delete role. It is assigned to " + userCount + " user(s).");
        }
        roleRepository.deleteById(id);
    }
    
    // Query Operations
    
    /**
     * Find paginated roles
     * @param page Page number
     * @param size Page size
     * @param sortBy Sort field
     * @param direction Sort direction
     * @return Page of role DTOs
     */
    @Transactional(readOnly = true)
    public Page<RoleDTO> findPaginatedRoles(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Role> rolePage = roleRepository.findAll(pageable);
        return rolePage.map(RoleMapper::toDTO);
    }
    
    /**
     * Search roles by term
     * @param searchTerm Search term
     * @param page Page number
     * @param size Page size
     * @param sortBy Sort field
     * @param direction Sort direction
     * @return Page of role DTOs matching search
     */
    @Transactional(readOnly = true)
    public Page<RoleDTO> searchRoles(String searchTerm, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Role> rolePage = roleRepository.searchRoles(searchTerm, pageable);
        return rolePage.map(RoleMapper::toDTO);
    }
    
    /**
     * Find roles with users
     * @return List of role DTOs that have users assigned
     */
    @Transactional(readOnly = true)
    public List<RoleDTO> findRolesWithUsers() {
        List<Role> roles = roleRepository.findRolesWithUsers();
        return RoleMapper.toDTOList(roles);
    }
    
    /**
     * Find roles without users
     * @return List of role DTOs that have no users assigned
     */
    @Transactional(readOnly = true)
    public List<RoleDTO> findRolesWithoutUsers() {
        List<Role> roles = roleRepository.findRolesWithoutUsers();
        return RoleMapper.toDTOList(roles);
    }
    
    /**
     * Find default user roles
     * @return List of default role DTOs
     */
    @Transactional(readOnly = true)
    public List<RoleDTO> findDefaultUserRoles() {
        List<Role> roles = roleRepository.findDefaultUserRoles();
        return RoleMapper.toDTOList(roles);
    }
    
    /**
     * Find admin roles
     * @return List of admin role DTOs
     */
    @Transactional(readOnly = true)
    public List<RoleDTO> findAdminRoles() {
        List<Role> roles = roleRepository.findAdminRoles();
        return RoleMapper.toDTOList(roles);
    }
    
    /**
     * Get total number of roles
     * @return Total role count
     */
    @Transactional(readOnly = true)
    public long getTotalRoles() {
        return roleRepository.count();
    }
    
    /**
     * Get all roles
     * @return List of all roles
     */
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    
    /**
     * Get all roles with pagination
     * @param pageable Pagination information
     * @return Page of roles
     */
    @Transactional(readOnly = true)
    public Page<Role> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }
    
    /**
     * Search roles by keyword
     * @param keyword Search keyword
     * @param pageable Pagination information
     * @return Page of roles matching the search
     */
    @Transactional(readOnly = true)
    public Page<Role> searchRoles(String keyword, Pageable pageable) {
        return roleRepository.searchRoles(keyword, pageable);
    }
    
    /**
     * Get role by ID
     * @param id Role ID
     * @return Optional containing the role if found
     */
    @Transactional(readOnly = true)
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id.intValue());
    }
    
    /**
     * Get role by name
     * @param name Role name
     * @return Optional containing the role if found
     */
    @Transactional(readOnly = true)
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
    
    /**
     * Check if role exists by name
     * @param name Role name
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
    
    /**
     * Create a new role
     * @param roleDTO Role data
     * @return Created role
     */
    @Transactional
    public Role createRole(RoleDTO roleDTO) {
        Role role = RoleMapper.toEntity(roleDTO);
        return roleRepository.save(role);
    }
    
    /**
     * Update role
     * @param roleDTO Role data
     * @return Updated role
     */
    @Transactional
    public Role updateRole(RoleDTO roleDTO) {
        Optional<Role> existingRoleOpt = roleRepository.findById(roleDTO.getId());
        if (existingRoleOpt.isEmpty()) {
            throw new IllegalArgumentException("Role not found");
        }
        
        Role existingRole = existingRoleOpt.get();
        existingRole.setName(roleDTO.getName());
        existingRole.setDescription(roleDTO.getDescription());
        
        return roleRepository.save(existingRole);
    }
    
    /**
     * Delete role
     * @param id Role ID
     */
    @Transactional
    public void deleteRole(Long id) {
        Optional<Role> roleOpt = roleRepository.findById(id.intValue());
        if (roleOpt.isEmpty()) {
            throw new IllegalArgumentException("Role not found");
        }
        
        Role role = roleOpt.get();
        if (!role.getUsers().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete role with assigned users");
        }
        
        roleRepository.deleteById(id.intValue());
    }
    
    /**
     * Get users by role with pagination
     * @param role Role entity
     * @param pageable Pagination information
     * @return Page of users with the specified role
     */
    @Transactional(readOnly = true)
    public Page<com.congdinh.models.User> getUsersByRole(Role role, Pageable pageable) {
        return roleRepository.findUsersByRole(role.getName(), pageable);
    }
    
    // Validation Operations
    
    /**
     * Check if a role exists by ID
     * @param id Role ID
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean roleExistsById(Integer id) {
        return roleRepository.existsById(id);
    }
    
    /**
     * Check if role name exists
     * @param name Role name to check
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean roleNameExists(String name) {
        return roleRepository.existsByName(name);
    }
    
    /**
     * Check if role name exists excluding a specific role
     * @param name Role name to check
     * @param id Role ID to exclude
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean roleNameExistsExcludingId(String name, Integer id) {
        return roleRepository.existsByNameAndIdNot(name, id);
    }
    
    /**
     * Count users in a role
     * @param roleId Role ID
     * @return Number of users in the role
     */
    @Transactional(readOnly = true)
    public long countUsersInRole(Integer roleId) {
        return roleRepository.countUsersInRole(roleId);
    }
    
    /**
     * Create default roles if they don't exist
     */
    public void createDefaultRoles() {
        createRoleIfNotExists("ADMIN", "System Administrator with full access");
        createRoleIfNotExists("USER", "Regular user with basic access");
        createRoleIfNotExists("CUSTOMER", "Customer with shopping access");
        createRoleIfNotExists("MODERATOR", "Content moderator with limited admin access");
    }
    
    /**
     * Create a role if it doesn't exist
     * @param name Role name
     * @param description Role description
     */
    private void createRoleIfNotExists(String name, String description) {
        if (!roleRepository.existsByName(name)) {
            RoleDTO roleDTO = new RoleDTO(name, description);
            saveRole(roleDTO);
        }
    }
}
