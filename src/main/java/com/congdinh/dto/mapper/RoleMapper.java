package com.congdinh.dto.mapper;

import com.congdinh.dto.RoleDto;
import com.congdinh.models.Role;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between Role entity and RoleDto
 */
public class RoleMapper {
    
    /**
     * Convert Role entity to RoleDto
     * @param role Role entity
     * @return RoleDto
     */
    public static RoleDto toDto(Role role) {
        if (role == null) {
            return null;
        }
        
        return new RoleDto(
            role.getId(),
            role.getName(),
            role.getDescription()
        );
    }
    
    /**
     * Convert RoleDto to Role entity
     * @param roleDto RoleDto
     * @return Role entity
     */
    public static Role toEntity(RoleDto roleDto) {
        if (roleDto == null) {
            return null;
        }
        
        Role role = new Role();
        role.setId(roleDto.getId());
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());
        
        return role;
    }
    
    /**
     * Convert list of Role entities to list of RoleDto
     * @param roles List of Role entities
     * @return List of RoleDto
     */
    public static List<RoleDto> toDtoList(List<Role> roles) {
        if (roles == null) {
            return null;
        }
        
        return roles.stream()
                .map(RoleMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Update Role entity from RoleDto
     * @param role Role entity to update
     * @param roleDto RoleDto with updated data
     */
    public static void updateEntityFromDto(Role role, RoleDto roleDto) {
        if (role == null || roleDto == null) {
            return;
        }
        
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());
    }
}