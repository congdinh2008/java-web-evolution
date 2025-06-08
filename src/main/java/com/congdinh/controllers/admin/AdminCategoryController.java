package com.congdinh.controllers.admin;

import java.util.List;
import java.util.Optional;

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
    
    // Constants for model attributes and view names
    private static final String TITLE_ATTR = "title";
    private static final String CATEGORY_ATTR = "category";
    private static final String CATEGORIES_ATTR = "categories";
    private static final String IS_EDIT_ATTR = "isEdit";
    private static final String FORM_VIEW = "admin/category/form";
    private static final String REDIRECT_CATEGORIES = "redirect:/admin/categories";
    private static final String SUCCESS_MESSAGE_ATTR = "successMessage";
    private static final String ERROR_MESSAGE_ATTR = "errorMessage";
    
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
            
            // Calculate stats for dashboard cards using new service method
            long totalCategories = categoryService.getTotalCategoryCount();
            // For now, we'll use simple logic for active/parent/sub categories
            // This could be enhanced with proper repository methods if needed
            long activeCategories = totalCategories; // Assuming all categories are active
            long parentCategories = Math.max(1, totalCategories / 2); // Simple estimation
            long subCategories = totalCategories - parentCategories;
            
            model.addAttribute(CATEGORIES_ATTR, categoryPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", categoryPage.getTotalPages());
            model.addAttribute("totalElements", categoryPage.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("direction", direction);
            model.addAttribute("search", search);
            
            // Stats for dashboard cards
            model.addAttribute("totalCategories", totalCategories);
            model.addAttribute("activeCategories", activeCategories);
            model.addAttribute("parentCategories", parentCategories);
            model.addAttribute("subCategories", subCategories);
            
            model.addAttribute(TITLE_ATTR, "Admin - Category Management");
            
            return "admin/category/list";
            
        } catch (Exception e) {
            System.err.println("[AdminCategoryController] listCategories: Error getting categories");
            e.printStackTrace(System.err);
            
            model.addAttribute(ERROR_MESSAGE_ATTR, "Error loading categories. Please try again.");
            model.addAttribute(CATEGORIES_ATTR, List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalElements", 0);
            model.addAttribute("totalCategories", 0);
            model.addAttribute("activeCategories", 0);
            model.addAttribute("parentCategories", 0);
            model.addAttribute("subCategories", 0);
            model.addAttribute(TITLE_ATTR, "Admin - Category Management");
            
            return "admin/category/list";
        }
    }
    
    /**
     * Show form to add a new category
     */
    @GetMapping("/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute(TITLE_ATTR, "Admin - Add New Category");
        model.addAttribute(CATEGORY_ATTR, new CategoryDTO());
        model.addAttribute(IS_EDIT_ATTR, false);
        return FORM_VIEW;
    }
    
    /**
     * Process form submission to add a new category
     */
    @PostMapping("/add")
    public String addCategory(@ModelAttribute CategoryDTO category, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Validate required fields
            if (category.getName() == null || category.getName().trim().isEmpty()) {
                model.addAttribute(ERROR_MESSAGE_ATTR, "Category name is required");
                model.addAttribute(CATEGORY_ATTR, category);
                model.addAttribute(IS_EDIT_ATTR, false);
                return FORM_VIEW;
            }
            
            // Check if category name already exists
            if (categoryService.categoryNameExists(category.getName())) {
                model.addAttribute(ERROR_MESSAGE_ATTR, "Category name already exists");
                model.addAttribute(CATEGORY_ATTR, category);
                model.addAttribute(IS_EDIT_ATTR, false);
                return FORM_VIEW;
            }
            
            // Save new category
            CategoryDTO savedCategory = categoryService.saveCategory(category);
            System.out.println("[AdminCategoryController] addCategory: Category added successfully with ID: " + savedCategory.getId());
            
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE_ATTR, "Category added successfully!");
            return REDIRECT_CATEGORIES;
            
        } catch (Exception e) {
            System.err.println("[AdminCategoryController] addCategory: Error adding category");
            e.printStackTrace(System.err);
            
            model.addAttribute(ERROR_MESSAGE_ATTR, "Error adding category. Please try again.");
            model.addAttribute(CATEGORY_ATTR, category);
            model.addAttribute(IS_EDIT_ATTR, false);
            return FORM_VIEW;
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
                model.addAttribute(CATEGORY_ATTR, category);
                model.addAttribute(TITLE_ATTR, "Admin - Edit Category: " + category.getName());
                model.addAttribute(IS_EDIT_ATTR, true);
                
                return FORM_VIEW;
            } else {
                System.out.println("[AdminCategoryController] showEditCategoryForm: Category not found with ID: " + categoryId);
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE_ATTR, "Category not found");
                return REDIRECT_CATEGORIES;
            }
        } catch (Exception e) {
            System.err.println("[AdminCategoryController] showEditCategoryForm: Error getting category with ID: " + categoryId);
            e.printStackTrace(System.err);
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE_ATTR, "Error loading category");
            return REDIRECT_CATEGORIES;
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
                model.addAttribute(ERROR_MESSAGE_ATTR, "Category name is required");
                model.addAttribute(CATEGORY_ATTR, category);
                model.addAttribute(IS_EDIT_ATTR, true);
                return FORM_VIEW;
            }
            
            // Check if category exists
            if (!categoryService.categoryExistsById(category.getId())) {
                System.err.println("[AdminCategoryController] updateCategory: Category not found with ID: " + category.getId());
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE_ATTR, "Category not found");
                return REDIRECT_CATEGORIES;
            }
            
            // Check if category name already exists (excluding current category)
            if (categoryService.categoryNameExistsExcludingId(category.getName(), category.getId())) {
                model.addAttribute(ERROR_MESSAGE_ATTR, "Category name already exists");
                model.addAttribute(CATEGORY_ATTR, category);
                model.addAttribute(IS_EDIT_ATTR, true);
                return FORM_VIEW;
            }
            
            // Update category
            categoryService.saveCategory(category);
            System.out.println("[AdminCategoryController] updateCategory: Category updated successfully with ID: " + category.getId());
            
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE_ATTR, "Category updated successfully!");
            return REDIRECT_CATEGORIES;
            
        } catch (Exception e) {
            System.err.println("[AdminCategoryController] updateCategory: Error updating category");
            e.printStackTrace(System.err);
            
            model.addAttribute(ERROR_MESSAGE_ATTR, "Error updating category. Please try again.");
            model.addAttribute(CATEGORY_ATTR, category);
            model.addAttribute(IS_EDIT_ATTR, true);
            return FORM_VIEW;
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
