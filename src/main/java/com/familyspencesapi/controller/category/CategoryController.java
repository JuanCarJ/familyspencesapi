package com.familyspencesapi.controller.category;

import com.familyspencesapi.domain.category.Category;
import com.familyspencesapi.domain.category.CategoryType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/categorias")
public class CategoryController {

    // Datos quemados simulando una base de datos
    private static final List<Category> categories = new ArrayList<>();

    static {
        categories.add(new Category(UUID.randomUUID(), "Mercado", CategoryType.ALIMENTACION, "Gastos mensuales en compra de alimentos y productos del hogar"));
        categories.add(new Category(UUID.randomUUID(), "Pasajes", CategoryType.TRANSPORTE, "Pago de transporte público para el trabajo y los niños"));
        categories.add(new Category(UUID.randomUUID(), "Colegio de los niños", CategoryType.EDUCACION, "Pago anual de matrícula y mensualidades escolares"));
    }

    // GET ALL
    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categories);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable UUID id) {
        return categories.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(@PathVariable Category category) {
        category.setId(UUID.randomUUID()); // asigna un id simulado
        categories.add(category);

        Map<String, Object> response = new HashMap<>();
        response.put("id", category.getId());
        response.put("mensaje", "Categoria creada exitosamente");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateCategory(@PathVariable UUID id, @RequestBody Category category) {
        Optional<Category> existingCategory = categories.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();

        if (existingCategory.isPresent()) {
            Category c = existingCategory.get();
            c.setName(category.getName());
            c.setCategoryType(category.getCategoryType());
            c.setDescription(category.getDescription());

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Categoria actualizada exitosamente");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable UUID id) {
        boolean removed = categories.removeIf(c -> c.getId().equals(id));

        Map<String, String> response = new HashMap<>();
        if (removed) {
            response.put("mensaje", "Categoria eliminada exitosamente");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}