package com.congdinh.dto.mapper;

import com.congdinh.dto.RoleDTO;
import com.congdinh.models.Role;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between Role entity and RoleDTO
 */
public class RoleMapper {
    
    private RoleMapper() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Convert Role entity to RoleDTO
     * @param role Role entity
     * @return RoleDTO
     */
    public static RoleDTO toDTO(Role role) {
        if (role == null) {
            return null;
        }
        
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());
        roleDTO.setDescription(role.getDescription());
        roleDTO.setCreatedAt(role.getCreatedAt());
        roleDTO.setUpdatedAt(role.getUpdatedAt());
        roleDTO.setUserCount(role.getUsers() != null ? role.getUsers().size() : 0);
        roleDTO.setUsers(role.getUsers() != null ? role.getUsers().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toSet()) : null);
        
        return roleDTO;
    }
    
    /**
     * Convert RoleDTO to Role entity
     * @param roleDTO RoleDTO
     * @return Role entity
     */
    public static Role toEntity(RoleDTO roleDTO) {
        if (roleDTO == null) {
            return null;
        }
        
        Role role = new Role();
        role.setId(roleDTO.getId());
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        role.setCreatedAt(roleDTO.getCreatedAt());
        role.setUpdatedAt(roleDTO.getUpdatedAt());
        
        // Note: Users are not mapped here to avoid circular dependency issues
        
        return role;
    }
    
    /**
     * Convert a list of Role entities to a list of RoleDTOs
     * @param roles List of Role entities
     * @return List of RoleDTOs
     */
    public static List<RoleDTO> toDTOList(List<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(RoleMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert a set of Role entities to a set of RoleDTOs
     * @param roles Set of Role entities
     * @return Set of RoleDTOs
     */
    public static Set<RoleDTO> toDTOSet(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(RoleMapper::toDTO)
                .collect(Collectors.toSet());
    }
    
    /**
     * Convert a list of RoleDTOs to a list of Role entities
     * @param roleDTOs List of RoleDTOs
     * @return List of Role entities
     */
    public static List<Role> toEntityList(List<RoleDTO> roleDTOs) {
        if (roleDTOs == null) {
            return null;
        }
        return roleDTOs.stream()
                .map(RoleMapper::toEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert a set of RoleDTOs to a set of Role entities
     * @param roleDTOs Set of RoleDTOs
     * @return Set of Role entities
     */
    public static Set<Role> toEntitySet(Set<RoleDTO> roleDTOs) {
        if (roleDTOs == null) {
            return null;
        }
        return roleDTOs.stream()
                .map(RoleMapper::toEntity)
                .collect(Collectors.toSet());
    }
    
    /**
     * Update existing Role entity with RoleDTO data (preserves ID and timestamps)
     * @param existingRole Existing Role entity
     * @param roleDTO RoleDTO with updated data
     */
    public static void updateEntityFromDTO(Role existingRole, RoleDTO roleDTO) {
        if (existingRole == null || roleDTO == null) {
            return;
        }
        
        existingRole.setName(roleDTO.getName());
        existingRole.setDescription(roleDTO.getDescription());
        
        // Note: ID, timestamps, and users are not updated here
    }
}
