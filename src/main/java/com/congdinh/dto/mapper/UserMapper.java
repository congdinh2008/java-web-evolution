package com.congdinh.dto.mapper;

import com.congdinh.dto.UserDTO;
import com.congdinh.models.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between User entity and UserDTO
 */
public class UserMapper {
    
    private UserMapper() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Convert User entity to UserDTO
     * @param user User entity
     * @return UserDTO
     */
    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setEmail(user.getEmail());
        userDTO.setAddress(user.getAddress());
        userDTO.setEnabled(user.isEnabled());
        userDTO.setAccountNonExpired(user.isAccountNonExpired());
        userDTO.setAccountNonLocked(user.isAccountNonLocked());
        userDTO.setCredentialsNonExpired(user.isCredentialsNonExpired());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setLastLogin(user.getLastLogin());
        
        // Convert roles
        if (user.getRoles() != null) {
            userDTO.setRoles(user.getRoles().stream()
                    .map(RoleMapper::toDTO)
                    .collect(Collectors.toSet()));
        }
        
        return userDTO;
    }
    
    /**
     * Convert UserDTO to User entity (excludes password for security)
     * @param userDTO UserDTO
     * @return User entity
     */
    public static User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        
        User user = new User();
        user.setId(userDTO.getId());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setEmail(userDTO.getEmail());
        user.setAddress(userDTO.getAddress());
        user.setEnabled(userDTO.isEnabled());
        user.setAccountNonExpired(userDTO.isAccountNonExpired());
        user.setAccountNonLocked(userDTO.isAccountNonLocked());
        user.setCredentialsNonExpired(userDTO.isCredentialsNonExpired());
        user.setCreatedAt(userDTO.getCreatedAt());
        user.setUpdatedAt(userDTO.getUpdatedAt());
        user.setLastLogin(userDTO.getLastLogin());
        
        // Note: Password is not mapped for security reasons
        // Roles are not mapped here to avoid circular dependency issues
        
        return user;
    }
    
    /**
     * Convert UserDTO to User entity including password (for registration/update)
     * @param userDTO UserDTO
     * @param encodedPassword Encoded password
     * @return User entity
     */
    public static User toEntityWithPassword(UserDTO userDTO, String encodedPassword) {
        User user = toEntity(userDTO);
        if (user != null && encodedPassword != null) {
            user.setPassword(encodedPassword);
        }
        return user;
    }
    
    /**
     * Convert a list of User entities to a list of UserDTOs
     * @param users List of User entities
     * @return List of UserDTOs
     */
    public static List<UserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert a set of User entities to a set of UserDTOs
     * @param users Set of User entities
     * @return Set of UserDTOs
     */
    public static Set<UserDTO> toDTOSet(Set<User> users) {
        if (users == null) {
            return null;
        }
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toSet());
    }
    
    /**
     * Convert a list of UserDTOs to a list of User entities
     * @param userDTOs List of UserDTOs
     * @return List of User entities
     */
    public static List<User> toEntityList(List<UserDTO> userDTOs) {
        if (userDTOs == null) {
            return null;
        }
        return userDTOs.stream()
                .map(UserMapper::toEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Update existing User entity with UserDTO data (preserves ID and password)
     * @param existingUser Existing User entity
     * @param userDTO UserDTO with updated data
     */
    public static void updateEntityFromDTO(User existingUser, UserDTO userDTO) {
        if (existingUser == null || userDTO == null) {
            return;
        }
        
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setEnabled(userDTO.isEnabled());
        existingUser.setAccountNonExpired(userDTO.isAccountNonExpired());
        existingUser.setAccountNonLocked(userDTO.isAccountNonLocked());
        existingUser.setCredentialsNonExpired(userDTO.isCredentialsNonExpired());
        
        // Note: ID, password, timestamps, and roles are not updated here
    }
}
