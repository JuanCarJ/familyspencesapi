package com.familyspencesapi.controllers.categories;

import com.familyspencesapi.domain.categories.Category;
import com.familyspencesapi.service.CategoryService;
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
//    @GetMapping("/all")
//    public ResponseEntity<List<Category>> getAllCategories() {
//        return ResponseEntity.ok(categories);
//    }
//
//    // GET BY ID
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getCategoryById(@PathVariable UUID id) {
//        return categories.stream()
//                .filter(c -> c.getId().equals(id))
//                .findFirst()
//                .<ResponseEntity<?>>map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Map.of("mensaje", "Categoria no encontrada")));
//    }
//
//    // PUT
//    @PutMapping("/{id}")
//    public ResponseEntity<Map<String, String>> updateCategory(@PathVariable UUID id, @RequestBody Category category) {
//        Optional<Category> existingCategory = categories.stream()
//                .filter(c -> c.getId().equals(id))
//                .findFirst();
//
//        if (existingCategory.isPresent()) {
//            Category c = existingCategory.get();
//            c.setName(category.getName());
//            c.setCategoryType(category.getCategoryType());
//            c.setDescription(category.getDescription());
//
//            return ResponseEntity.ok(Map.of("mensaje", "Categoria actualizada exitosamente"));
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("mensaje", "Categoria no encontrada"));
//        }
//    }
//
//    // DELETE
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable UUID id) {
//        boolean removed = categories.removeIf(c -> c.getId().equals(id));
//
//        if (removed) {
//            return ResponseEntity.ok(Map.of("mensaje", "Categoria eliminada exitosamente"));
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("mensaje", "Categoria no encontrada"));
//        }
//    }
}
