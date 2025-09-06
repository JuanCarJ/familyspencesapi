package com.familyspencesapi.controllers.categories;

import com.familyspencesapi.domain.categories.Category;
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

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // POST
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        try {
            Category created = categoryService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "La categoría " + created.getName() + " ha sido creada exitosamente"));
        } catch (CategoryException ex) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", ex.getMessage()));
        }
    }

    // GET ALL
   @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
   }

   // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(categoryService.getCategoryById(id));
        } catch (CategoryException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        }
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable UUID id, @RequestBody Category category) {
        try {
            Category updated = categoryService.updateCategory(id, category);
            return ResponseEntity.ok(Map.of("mensaje", "La categoría " + updated.getName() + " ha sido actualizada exitosamente"));
        } catch (CategoryException ex) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", ex.getMessage()));
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable UUID id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(Map.of("mensaje", "La categoría ha sido eliminada exitosamente"));
        } catch (CategoryException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
        }
    }
}
