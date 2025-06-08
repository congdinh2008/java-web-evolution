package com.congdinh.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.congdinh.dto.UserDTO;
import com.congdinh.models.Role;
import com.congdinh.models.User;
import com.congdinh.services.UserService;
import com.congdinh.services.RoleService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Controller for user management operations
 * Accessible only to users with ADMIN role
 */
@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private final UserService userService;
    private final RoleService roleService;

    public UserManagementController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * Display list of users with pagination and search
     */
    @GetMapping
    public String listUsers(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "id") String sortBy,
                           @RequestParam(defaultValue = "asc") String sortDir,
                           @RequestParam(required = false) String search,
                           Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> userPage;
        
        if (search != null && !search.trim().isEmpty()) {
            userPage = userService.searchUsers(search.trim(), pageable);
            model.addAttribute("search", search);
        } else {
            userPage = userService.getAllUsers(pageable);
        }
        
        // Pass the full Page object so template can access pagination properties
        model.addAttribute("users", userPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalElements", userPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        // Add statistics data for the template
        model.addAttribute("totalUsers", userService.getTotalUsers());
        model.addAttribute("activeUsers", userService.getActiveUsers());
        model.addAttribute("inactiveUsers", userService.getInactiveUsers());
        model.addAttribute("adminUsers", userService.getAdminUsers());
        
        // Add roles data for the filter dropdown
        model.addAttribute("roles", roleService.getAllRoles());
        
        return "admin/user/list";
    }

    /**
     * Display form to create new user
     */
    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new UserDTO());
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/user/create";
    }

    /**
     * Process user creation
     */
    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("user") UserDTO userDTO,
                            BindingResult bindingResult,
                            @RequestParam(required = false) List<Long> roleIds,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleService.getAllRoles());
            return "admin/user/create";
        }
        
        try {
            // Check if user with email already exists
            if (userDTO.getEmail() != null && userService.existsByEmail(userDTO.getEmail())) {
                model.addAttribute("error", "A user with this email already exists!");
                model.addAttribute("roles", roleService.getAllRoles());
                return "admin/user/create";
            }
            
            // Check if user with phone number already exists
            if (userService.existsByPhoneNumber(userDTO.getPhoneNumber())) {
                model.addAttribute("error", "A user with this phone number already exists!");
                model.addAttribute("roles", roleService.getAllRoles());
                return "admin/user/create";
            }
            
            User newUser = userService.createUserWithRoles(userDTO, roleIds);
            redirectAttributes.addFlashAttribute("success", 
                "User '" + newUser.getFirstName() + " " + newUser.getLastName() + "' created successfully!");
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create user: " + e.getMessage());
            model.addAttribute("roles", roleService.getAllRoles());
            return "admin/user/create";
        }
        
        return "redirect:/admin/users";
    }

    /**
     * Display user details
     */
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.getUserById(id);
        
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/admin/users";
        }
        
        User user = userOptional.get();
        model.addAttribute("user", user);
        
        return "admin/user/view";
    }

    /**
     * Display form to edit user
     */
    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.getUserById(id);
        
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found!");
            return "redirect:/admin/users";
        }
        
        User user = userOptional.get();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setEmail(user.getEmail());
        userDTO.setAddress(user.getAddress());
        userDTO.setEnabled(user.isEnabled());
        
        model.addAttribute("user", userDTO);
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("userRoles", user.getRoles());
        
        return "admin/user/edit";
    }

    /**
     * Process user update
     */
    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id,
                           @Valid @ModelAttribute("user") UserDTO userDTO,
                           BindingResult bindingResult,
                           @RequestParam(required = false) List<Long> roleIds,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        if (bindingResult.hasErrors()) {
            Optional<User> userOptional = userService.getUserById(id);
            if (userOptional.isPresent()) {
                model.addAttribute("roles", roleService.getAllRoles());
                model.addAttribute("userRoles", userOptional.get().getRoles());
            }
            return "admin/user/edit";
        }
        
        try {
            userDTO.setId(id.intValue());
            User updatedUser = userService.updateUserWithRoles(userDTO, roleIds);
            redirectAttributes.addFlashAttribute("success", 
                "User '" + updatedUser.getFirstName() + " " + updatedUser.getLastName() + "' updated successfully!");
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update user: " + e.getMessage());
            Optional<User> userOptional = userService.getUserById(id);
            if (userOptional.isPresent()) {
                model.addAttribute("roles", roleService.getAllRoles());
                model.addAttribute("userRoles", userOptional.get().getRoles());
            }
            return "admin/user/edit";
        }
        
        return "redirect:/admin/users";
    }

    /**
     * Toggle user enabled status
     */
    @PostMapping("/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.toggleUserStatus(id);
            String status = user.isEnabled() ? "enabled" : "disabled";
            redirectAttributes.addFlashAttribute("success", 
                "User '" + user.getFirstName() + " " + user.getLastName() + "' " + status + " successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user status: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    /**
     * Reset user password
     */
    @PostMapping("/{id}/reset-password")
    public String resetPassword(@PathVariable Long id, 
                               @RequestParam String newPassword,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.resetUserPassword(id, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password reset successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reset password: " + e.getMessage());
        }
        
        return "redirect:/admin/users/" + id;
    }

    /**
     * Delete user (soft delete - disable account)
     */
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOptional = userService.getUserById(id);
            
            if (userOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
                return "redirect:/admin/users";
            }
            
            User user = userOptional.get();
            userService.disableUser(id);
            redirectAttributes.addFlashAttribute("success", 
                "User '" + user.getFirstName() + " " + user.getLastName() + "' disabled successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to disable user: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }
}
