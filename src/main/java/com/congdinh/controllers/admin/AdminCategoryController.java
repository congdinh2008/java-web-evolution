package com.congdinh.controllers.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.congdinh.dto.CategoryDTO;
import com.congdinh.services.CategoryService;

/**
 * Admin controller for handling category-related operations
 */
@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    
    private final CategoryService categoryService;
    
    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    /**
     * List all categories with search and pagination
     */
    @GetMapping
    public String listCategories(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            Model model) {
        
        try {
            Page<CategoryDTO> categoryPage;
            
            if (search.trim().isEmpty()) {
                categoryPage = categoryService.findPaginatedCategories(page, size, sortBy, direction);
            } else {
                categoryPage = categoryService.searchCategoriesByName(search, page, size, sortBy, direction);
            }
            
            model.addAttribute("categories", categoryPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", categoryPage.getTotalPages());
            model.addAttribute("totalElements", categoryPage.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("direction", direction);
            model.addAttribute("search", search);
            model.addAttribute("title", "Admin - Category Management");
            
            return "admin/category/list";
            
        } catch (Exception e) {
            System.err.println("[AdminCategoryController] listCategories: Error getting categories");
            e.printStackTrace(System.err);
            
            model.addAttribute("errorMessage", "Error loading categories. Please try again.");
            model.addAttribute("categories", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalElements", 0);
            model.addAttribute("title", "Admin - Category Management");
            
            return "admin/category/list";
        }
    }
    
    /**
     * Show form to add a new category
     */
    @GetMapping("/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("title", "Admin - Add New Category");
        model.addAttribute("category", new CategoryDTO());
        model.addAttribute("isEdit", false);
        return "admin/category/form";
    }
    
    /**
     * Process form submission to add a new category
     */
    @PostMapping("/add")
    public String addCategory(@ModelAttribute CategoryDTO category, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Validate required fields
            if (category.getName() == null || category.getName().trim().isEmpty()) {
                model.addAttribute("errorMessage", "Category name is required");
                model.addAttribute("category", category);
                model.addAttribute("isEdit", false);
                return "admin/category/form";
            }
            
            // Check if category name already exists
            if (categoryService.categoryNameExists(category.getName())) {
                model.addAttribute("errorMessage", "Category name already exists");
                model.addAttribute("category", category);
                model.addAttribute("isEdit", false);
                return "admin/category/form";
            }
            
            // Save new category
            CategoryDTO savedCategory = categoryService.saveCategory(category);
            System.out.println("[AdminCategoryController] addCategory: Category added successfully with ID: " + savedCategory.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", "Category added successfully!");
            return "redirect:/admin/categories";
            
        } catch (Exception e) {
            System.err.println("[AdminCategoryController] addCategory: Error adding category");
            e.printStackTrace(System.err);
            
            model.addAttribute("errorMessage", "Error adding category. Please try again.");
            model.addAttribute("category", category);
            model.addAttribute("isEdit", false);
            return "admin/category/form";
        }
    }
    
    /**
     * Show form to edit an existing category
     */
    @GetMapping("/edit")
    public String showEditCategoryForm(@RequestParam("id") Integer categoryId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<CategoryDTO> categoryOpt = categoryService.findCategoryById(categoryId);
            
            if (categoryOpt.isPresent()) {
                CategoryDTO category = categoryOpt.get();
                model.addAttribute("category", category);
                model.addAttribute("title", "Admin - Edit Category: " + category.getName());
                model.addAttribute("isEdit", true);
                
                return "admin/category/form";
            } else {
                System.out.println("[AdminCategoryController] showEditCategoryForm: Category not found with ID: " + categoryId);
                redirectAttributes.addFlashAttribute("errorMessage", "Category not found");
                return "redirect:/admin/categories";
            }
        } catch (Exception e) {
            System.err.println("[AdminCategoryController] showEditCategoryForm: Error getting category with ID: " + categoryId);
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute("errorMessage", "Error loading category");
            return "redirect:/admin/categories";
        }
    }
    
    /**
     * Process form submission to update an existing category
     */
    @PostMapping("/update")
    public String updateCategory(@ModelAttribute CategoryDTO category, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Validate required fields
            if (category.getName() == null || category.getName().trim().isEmpty()) {
                model.addAttribute("errorMessage", "Category name is required");
                model.addAttribute("category", category);
                model.addAttribute("isEdit", true);
                return "admin/category/form";
            }
            
            // Check if category exists
            if (!categoryService.categoryExistsById(category.getId())) {
                System.err.println("[AdminCategoryController] updateCategory: Category not found with ID: " + category.getId());
                redirectAttributes.addFlashAttribute("errorMessage", "Category not found");
                return "redirect:/admin/categories";
            }
            
            // Check if category name already exists (excluding current category)
            if (categoryService.categoryNameExistsExcludingId(category.getName(), category.getId())) {
                model.addAttribute("errorMessage", "Category name already exists");
                model.addAttribute("category", category);
                model.addAttribute("isEdit", true);
                return "admin/category/form";
            }
            
            // Update category
            categoryService.saveCategory(category);
            System.out.println("[AdminCategoryController] updateCategory: Category updated successfully with ID: " + category.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully!");
            return "redirect:/admin/categories";
            
        } catch (Exception e) {
            System.err.println("[AdminCategoryController] updateCategory: Error updating category");
            e.printStackTrace(System.err);
            
            model.addAttribute("errorMessage", "Error updating category. Please try again.");
            model.addAttribute("category", category);
            model.addAttribute("isEdit", true);
            return "admin/category/form";
        }
    }
    
    /**
     * Delete a category
     */
    @PostMapping("/delete")
    @ResponseBody
    public String deleteCategory(@RequestParam("id") Integer categoryId) {
        try {
            // Check if category exists
            if (!categoryService.categoryExistsById(categoryId)) {
                return "error:Category not found";
            }
            
            // Check if category has products
            long productCount = categoryService.countProductsInCategory(categoryId);
            if (productCount > 0) {
                return "error:Cannot delete category with " + productCount + " products. Please move or delete products first.";
            }
            
            // Delete category
            categoryService.deleteCategoryById(categoryId);
            System.out.println("[AdminCategoryController] deleteCategory: Category deleted successfully with ID: " + categoryId);
            
            return "success:Category deleted successfully!";
            
        } catch (Exception e) {
            System.err.println("[AdminCategoryController] deleteCategory: Error deleting category with ID: " + categoryId);
            e.printStackTrace(System.err);
            return "error:Error deleting category. Please try again.";
        }
    }
}
