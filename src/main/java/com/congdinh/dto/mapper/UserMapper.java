package com.congdinh.dto.mapper;

import com.congdinh.dto.UserDto;
import com.congdinh.dto.UserRegistrationDto;
import com.congdinh.models.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between User entity and User DTOs
 */
public class UserMapper {
    
    /**
     * Convert User entity to UserDto
     * @param user User entity
     * @return UserDto
     */
    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserDto(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhoneNumber(),
            user.getDateOfBirth(),
            user.getAddress(),
            user.getRoles().stream()
                .map(RoleMapper::toDto)
                .collect(Collectors.toSet())
        );
    }
    
    /**
     * Convert UserDto to User entity
     * @param userDto UserDto
     * @return User entity
     */
    public static User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        
        User user = new User();
        user.setId(userDto.getId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setAddress(userDto.getAddress());
        
        return user;
    }
    
    /**
     * Convert UserRegistrationDto to User entity
     * @param registrationDto UserRegistrationDto
     * @return User entity
     */
    public static User toEntity(UserRegistrationDto registrationDto) {
        if (registrationDto == null) {
            return null;
        }
        
        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setPassword(registrationDto.getPassword());
        user.setDateOfBirth(registrationDto.getDateOfBirth());
        user.setAddress(registrationDto.getAddress());
        
        return user;
    }
    
    /**
     * Convert list of User entities to list of UserDto
     * @param users List of User entities
     * @return List of UserDto
     */
    public static List<UserDto> toDtoList(List<User> users) {
        if (users == null) {
            return null;
        }
        
        return users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Update User entity from UserDto
     * @param user User entity to update
     * @param userDto UserDto with updated data
     */
    public static void updateEntityFromDto(User user, UserDto userDto) {
        if (user == null || userDto == null) {
            return;
        }
        
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setAddress(userDto.getAddress());
    }
}