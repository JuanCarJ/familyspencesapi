package com.familyspencesapi.controllers.categories;

import com.familyspencesapi.domain.categories.BudgetPeriod;
import com.familyspencesapi.domain.categories.Category;
import com.familyspencesapi.domain.categories.CategorySummary;
import com.familyspencesapi.domain.categories.CategoryType;
import com.familyspencesapi.messages.categories.CategoryMessageSender;
import com.familyspencesapi.service.category.CategoryService;
import com.familyspencesapi.utils.CategoryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMessageSender categoryMessageSender;
    private static final String UNEXPECTED_ERROR = "Unexpected error";
    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "mensaje";

    public CategoryController(CategoryService categoryService, CategoryMessageSender categoryMessageSender) {
        this.categoryService = categoryService;
        this.categoryMessageSender = categoryMessageSender;
    }

    @PostMapping
    public ResponseEntity<Object> createCategory(
            @RequestParam(required = false) UUID familyId,
            @RequestBody Category category
    ) {
        try {
            category.setFamilyId(familyId);
            Category created = categoryService.createCategory(category);

            categoryMessageSender.sendCategoryCreated(created);

            String message = familyId == null
                    ? "La categoría global " + created.getName() + " ha sido creada exitosamente"
                    : "La categoría " + created.getName() + " ha sido creada exitosamente para la familia";

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(MESSAGE_KEY, message));
        } catch (CategoryException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(MESSAGE_KEY, ex.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/global")
    public ResponseEntity<List<Category>> getGlobalCategories() {
        return ResponseEntity.ok(categoryService.getGlobalCategories());
    }

    @GetMapping("/family/{familyId}")
    public ResponseEntity<List<Category>> getFamilyCategories(@PathVariable UUID familyId) {
        return ResponseEntity.ok(categoryService.getFamilyCategories(familyId));
    }

    @GetMapping("/for-family/{familyId}")
    public ResponseEntity<List<Category>> getCategoriesForFamily(@PathVariable UUID familyId) {
        return ResponseEntity.ok(categoryService.getCategoriesForFamily(familyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCategoryById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(categoryService.getCategoryById(id));
        } catch (CategoryException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(MESSAGE_KEY, ex.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<CategorySummary>> getCategoryIds() {
        return ResponseEntity.ok(categoryService.getCategoryIds());
    }

    @GetMapping("/list/for-family/{familyId}")
    public ResponseEntity<List<CategorySummary>> getCategoryIdsForFamily(@PathVariable UUID familyId) {
        return ResponseEntity.ok(categoryService.getCategoryIdsForFamily(familyId));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Category>> filter(
            @RequestParam(required = false) CategoryType type,
            @RequestParam(required = false) BudgetPeriod period
    ) {
        List<Category> categories = categoryService.getFiltered(type, period);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/filter/family/{familyId}")
    public ResponseEntity<List<Category>> filterForFamily(
            @PathVariable UUID familyId,
            @RequestParam(required = false) CategoryType type,
            @RequestParam(required = false) BudgetPeriod period
    ) {
        List<Category> categories = categoryService.getFilteredForFamily(familyId, type, period);
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCategory(
            @PathVariable UUID id,
            @RequestBody Category category
    ) {
        try {
            Category updated = categoryService.updateCategory(id, category);

            categoryMessageSender.sendCategoryUpdated(updated);

            return ResponseEntity.ok(Map.of(MESSAGE_KEY, "La categoría ha sido actualizada exitosamente"));
        } catch (CategoryException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(MESSAGE_KEY, ex.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCategory(@PathVariable UUID id) {
        try {
            Category category = categoryService.getCategoryById(id);
            try {
                categoryService.deleteCategory(id);
            } catch (CategoryException ex) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of(MESSAGE_KEY, ex.getMessage()));
            }

            Map<String, String> deleteInfo = new HashMap<>();
            deleteInfo.put("categoryId", id.toString());
            if (category.getFamilyId() != null) {
                deleteInfo.put("familyId", category.getFamilyId().toString());
            }
            categoryMessageSender.sendCategoryDeleted(deleteInfo);

            return ResponseEntity.ok(Map.of(MESSAGE_KEY, "La categoría ha sido eliminada exitosamente"));
        } catch (CategoryException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(MESSAGE_KEY, ex.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }
}