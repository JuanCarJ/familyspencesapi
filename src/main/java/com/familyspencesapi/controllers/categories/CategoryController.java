package com.familyspencesapi.controllers.categories;

import com.familyspencesapi.domain.categories.BudgetPeriod;
import com.familyspencesapi.domain.categories.Category;
import com.familyspencesapi.domain.categories.CategoryType;
import com.familyspencesapi.messages.categories.CategoryMessageSender;
import com.familyspencesapi.service.category.CategoryService;
import com.familyspencesapi.utils.CategoryException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMessageSender categoryMessageSender;
    private static final String UNEXPECTED_ERROR = "Unexpected error";
    private static final String ERROR_KEY = "error";

    public CategoryController(CategoryService categoryService, CategoryMessageSender categoryMessageSender) {
        this.categoryService = categoryService;
        this.categoryMessageSender = categoryMessageSender;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(@RequestParam UUID familyId) {
        List<Category> categories = categoryService.getCategoriesForFamily(familyId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Object> getCategory(
            @RequestParam UUID familyId,
            @PathVariable UUID categoryId
    ) {
        try {
            Category category = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(category);
        } catch (CategoryException ex) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Object> createCategory(
            @RequestParam UUID familyId,
            @RequestBody Category category
    ) {
        try {
            category.setFamilyId(familyId);

            Category createdCategory = categoryService.createCategory(category);

            categoryMessageSender.sendCategoryCreated(createdCategory);

            return ResponseEntity.ok(createdCategory);

        } catch (CategoryException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Object> updateCategory(
            @RequestParam UUID familyId,
            @PathVariable UUID categoryId,
            @RequestBody Category category
    ) {
        try {
            category.setFamilyId(familyId);

            Category updatedCategory = categoryService.updateCategory(categoryId, category);

            categoryMessageSender.sendCategoryUpdated(updatedCategory);

            return ResponseEntity.ok(updatedCategory);

        } catch (CategoryException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Object> deleteCategory(
            @RequestParam UUID familyId,
            @PathVariable UUID categoryId
    ) {
        try {
            categoryService.deleteCategory(categoryId);

            categoryMessageSender.sendCategoryDeleted(
                    Map.of("familyId", familyId.toString(), "categoryId", categoryId.toString())
            );

            return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));

        } catch (CategoryException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Category>> filterForFamily(
            @RequestParam UUID familyId, // Obligatorio
            @RequestParam(required = false) CategoryType type,
            @RequestParam(required = false) BudgetPeriod period
    ) {
        List<Category> categories = categoryService.getFilteredForFamily(familyId, type, period);
        return ResponseEntity.ok(categories);
    }
}