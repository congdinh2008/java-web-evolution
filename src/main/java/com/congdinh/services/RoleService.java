package com.congdinh.services;

import com.congdinh.dto.RoleDto;
import com.congdinh.dto.mapper.RoleMapper;
import com.congdinh.models.Role;
import com.congdinh.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    /**
     * Find all roles
     * @return List of all roles as DTOs
     */
    @Transactional(readOnly = true)
    public List<RoleDto> findAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return RoleMapper.toDtoList(roles);
    }
    
    /**
     * Find role by ID
     * @param id Role ID
     * @return Optional containing the role DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<RoleDto> findRoleById(Long id) {
        Optional<Role> roleOpt = roleRepository.findById(id);
        return roleOpt.map(RoleMapper::toDto);
    }
    
    /**
     * Find role by name
     * @param name Role name
     * @return Optional containing the role DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<RoleDto> findRoleByName(String name) {
        Optional<Role> roleOpt = roleRepository.findByName(name);
        return roleOpt.map(RoleMapper::toDto);
    }
    
    /**
     * Save a role (insert or update)
     * @param roleDto Role DTO to save
     * @return Saved role DTO with ID
     */
    public RoleDto saveRole(RoleDto roleDto) {
        Role role = RoleMapper.toEntity(roleDto);
        Role savedRole = roleRepository.save(role);
        return RoleMapper.toDto(savedRole);
    }
    
    /**
     * Update an existing role
     * @param id Role ID to update
     * @param roleDto Role DTO with updated data
     * @return Updated role DTO
     * @throws RuntimeException if role not found
     */
    public RoleDto updateRole(Long id, RoleDto roleDto) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        
        RoleMapper.updateEntityFromDto(existingRole, roleDto);
        Role updatedRole = roleRepository.save(existingRole);
        return RoleMapper.toDto(updatedRole);
    }
    
    /**
     * Delete a role by ID
     * @param id Role ID to delete
     * @throws RuntimeException if role not found
     */
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }
    
    /**
     * Check if a role exists with the given name
     * @param name Role name to check
     * @return true if role exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }
    
    /**
     * Create default roles if they don't exist
     */
    public void createDefaultRoles() {
        if (!existsByName("ROLE_USER")) {
            RoleDto userRole = new RoleDto(null, "ROLE_USER", "Default user role");
            saveRole(userRole);
        }
        
        if (!existsByName("ROLE_ADMIN")) {
            RoleDto adminRole = new RoleDto(null, "ROLE_ADMIN", "Administrator role");
            saveRole(adminRole);
        }
    }
}