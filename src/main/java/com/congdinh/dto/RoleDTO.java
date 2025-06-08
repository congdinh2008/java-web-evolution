package com.congdinh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object for Role
 * Used to transfer role data between layers without exposing domain model details
 */
public class RoleDTO {
    
    private Integer id;
    
    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name must not exceed 50 characters")
    private String name;
    
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private int userCount; // Number of users with this role

    private Set<UserDTO> users; // Optional: if you want to include users in the role
    
    // Constructors
    public RoleDTO() {
    }
    
    public RoleDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public RoleDTO(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    public RoleDTO(Integer id, String name, String description, int userCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userCount = userCount;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public int getUserCount() {
        return userCount;
    }
    
    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public Set<UserDTO> getUsers() {
        return users;
    }
    
    public void setUsers(Set<UserDTO> users) {
        this.users = users;
    }
    
    @Override
    public String toString() {
        return "RoleDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", userCount=" + userCount +
                '}';
    }
}
