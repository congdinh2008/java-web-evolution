package com.congdinh.controllers.admin;

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

import com.congdinh.dto.RoleDTO;
import com.congdinh.models.Role;
import com.congdinh.services.RoleService;

import jakarta.validation.Valid;
import java.util.Optional;

/**
 * Controller for role management operations
 * Accessible only to users with ADMIN role
 */
@Controller
@RequestMapping("/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleManagementController {

    private final RoleService roleService;

    public RoleManagementController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Display list of roles with pagination and search
     */
    @GetMapping
    public String listRoles(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "id") String sortBy,
                           @RequestParam(defaultValue = "asc") String sortDir,
                           @RequestParam(required = false) String search,
                           Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Role> rolePage;
        
        if (search != null && !search.trim().isEmpty()) {
            rolePage = roleService.searchRoles(search.trim(), pageable);
            model.addAttribute("search", search);
        } else {
            rolePage = roleService.getAllRoles(pageable);
        }
        
        model.addAttribute("roles", rolePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rolePage.getTotalPages());
        model.addAttribute("totalElements", rolePage.getTotalElements());
        model.addAttribute("totalRoles", (int) rolePage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "admin/role/list";
    }

    /**
     * Display form to create new role
     */
    @GetMapping("/create")
    public String createRoleForm(Model model) {
        model.addAttribute("role", new RoleDTO());
        return "admin/role/create";
    }

    /**
     * Process role creation
     */
    @PostMapping("/create")
    public String createRole(@Valid @ModelAttribute("role") RoleDTO roleDTO,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        if (bindingResult.hasErrors()) {
            return "admin/role/create";
        }
        
        try {
            // Check if role with name already exists
            if (roleService.existsByName(roleDTO.getName())) {
                model.addAttribute("error", "A role with this name already exists!");
                return "admin/role/create";
            }
            
            Role newRole = roleService.createRole(roleDTO);
            redirectAttributes.addFlashAttribute("success", 
                "Role '" + newRole.getName() + "' created successfully!");
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create role: " + e.getMessage());
            return "admin/role/create";
        }
        
        return "redirect:/admin/roles";
    }

    /**
     * Display role details
     */
    @GetMapping("/{id}")
    public String viewRole(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Role> roleOptional = roleService.getRoleById(id);
        
        if (roleOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Role not found!");
            return "redirect:/admin/roles";
        }
        
        Role role = roleOptional.get();
        model.addAttribute("role", role);
        model.addAttribute("userCount", role.getUsers().size());
        
        return "admin/role/view";
    }

    /**
     * Display form to edit role
     */
    @GetMapping("/{id}/edit")
    public String editRoleForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Role> roleOptional = roleService.getRoleById(id);
        
        if (roleOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Role not found!");
            return "redirect:/admin/roles";
        }
        
        Role role = roleOptional.get();
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());
        roleDTO.setDescription(role.getDescription());
        
        model.addAttribute("role", roleDTO);
        
        return "admin/role/edit";
    }

    /**
     * Process role update
     */
    @PostMapping("/{id}/edit")
    public String updateRole(@PathVariable Long id,
                           @Valid @ModelAttribute("role") RoleDTO roleDTO,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        if (bindingResult.hasErrors()) {
            return "admin/role/edit";
        }
        
        try {
            // Check if another role with the same name exists
            Optional<Role> existingRole = roleService.getRoleByName(roleDTO.getName());
            if (existingRole.isPresent() && !existingRole.get().getId().equals(id)) {
                model.addAttribute("error", "A role with this name already exists!");
                return "admin/role/edit";
            }
            
            roleDTO.setId(id.intValue());
            Role updatedRole = roleService.updateRole(roleDTO);
            redirectAttributes.addFlashAttribute("success", 
                "Role '" + updatedRole.getName() + "' updated successfully!");
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update role: " + e.getMessage());
            return "admin/role/edit";
        }
        
        return "redirect:/admin/roles";
    }

    /**
     * Delete role (only if no users are assigned)
     */
    @PostMapping("/{id}/delete")
    public String deleteRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Role> roleOptional = roleService.getRoleById(id);
            
            if (roleOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Role not found!");
                return "redirect:/admin/roles";
            }
            
            Role role = roleOptional.get();
            
            // Check if role has users assigned
            if (!role.getUsers().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    "Cannot delete role '" + role.getName() + "' because it has " + 
                    role.getUsers().size() + " user(s) assigned to it!");
                return "redirect:/admin/roles";
            }
            
            roleService.deleteRole(id);
            redirectAttributes.addFlashAttribute("success", 
                "Role '" + role.getName() + "' deleted successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete role: " + e.getMessage());
        }
        
        return "redirect:/admin/roles";
    }

    /**
     * Display users assigned to a role
     */
    @GetMapping("/{id}/users")
    public String viewRoleUsers(@PathVariable Long id, 
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model, 
                               RedirectAttributes redirectAttributes) {
        
        Optional<Role> roleOptional = roleService.getRoleById(id);
        
        if (roleOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Role not found!");
            return "redirect:/admin/roles";
        }
        
        Role role = roleOptional.get();
        Pageable pageable = PageRequest.of(page, size);
        
        // Get users for this role with pagination
        var usersPage = roleService.getUsersByRole(role, pageable);
        
        model.addAttribute("role", role);
        model.addAttribute("users", usersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usersPage.getTotalPages());
        model.addAttribute("totalElements", usersPage.getTotalElements());
        model.addAttribute("size", size);
        
        return "admin/role/users";
    }
}
