package com.familyspencesapi.service.category;

import com.familyspencesapi.domain.categories.Category;
import com.familyspencesapi.utils.CategoryException;

import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}0-9 ]+$");

    private final List<Category> categories = new ArrayList<>();

    public CategoryService() {
        categories.add(new Category(UUID.randomUUID(), "Mercado", com.familyspencesapi.domain.categories.CategoryType.ALIMENTACION, "Gastos mensuales en compra de alimentos y productos del hogar"));
        categories.add(new Category(UUID.randomUUID(), "Pasajes", com.familyspencesapi.domain.categories.CategoryType.TRANSPORTE, "Pago de transporte público para el trabajo y los niños"));
        categories.add(new Category(UUID.randomUUID(), "Colegio de los niños", com.familyspencesapi.domain.categories.CategoryType.EDUCACION, "Pago anual de matrícula y mensualidades escolares"));
    }

    // POST - Crear Categoria
    public Category createCategory(Category category) {
        validateCategory(category);

        category.setId(UUID.randomUUID());
        categories.add(category);

        return category;
    }

    // GET ALL - Consulta todas las categorias
    public List<Category> getAllCategories() {
        return categories;
    }

    // GET BY ID - Retrieve a category by its ID
    public Category getCategoryById(UUID id) {
        return categories.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new CategoryException("Categoría no encontrada"));
    }

    // PUT - Actualiza una categoria existente
    public Category updateCategory(UUID id, Category updates) {
        Category existing = getCategoryById(id); // lanza excepción si no existe
        validateCategoryUpdate(updates, existing);

        if (updates.getName() != null) {
            existing.setName(updates.getName());
        }
        if (updates.getCategoryType() != null) {
            existing.setCategoryType(updates.getCategoryType());
        }
        if (updates.getDescription() != null) {
            existing.setDescription(updates.getDescription());
        }

        return existing;
    }

    public void deleteCategory(UUID id) {
        Category existing = getCategoryById(id); // lanza excepción si no existe
        categories.remove(existing);
    }



    private void validateCategoryUpdate(Category updates, Category existing) {
        if (updates.getName() != null) {
            if (updates.getName().trim().isEmpty()) {
                throw new CategoryException("El nombre de la categoría es obligatorio y no puede estar vacío");
            }
            if (!NAME_PATTERN.matcher(updates.getName()).matches()) {
                throw new CategoryException("El nombre de la categoría es inválido");
            }
            if (updates.getName().length() < 3 || updates.getName().length() > 50) {
                throw new CategoryException("El nombre de la categoría debe tener entre 3 y 50 caracteres");
            }
        } else if (existing.getName() == null) {
            throw new CategoryException("El nombre de la categoría es obligatorio");
        }


        if (updates.getCategoryType() != null) {
            // válido porque es un enum
        } else if (existing.getCategoryType() == null) {
            throw new CategoryException("El tipo de la categoría es obligatorio");
        }

        if (updates.getDescription() != null && updates.getDescription().length() > 255) {
            throw new CategoryException("La descripción de la categoría no puede exceder los 255 caracteres");
        }
    }

    private void validateCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()){
            throw new CategoryException("El nombre de la categoria es obligatorio y no puede estar vacío");
        }
        if (!NAME_PATTERN.matcher(category.getName()).matches()) {
            throw new CategoryException("El nombre de la categoría es inválido");
        }
        if (category.getName().length() < 3 || category.getName().length() > 50) {
            throw new CategoryException("El nombre de la categoría debe tener entre 3 y 50 caracteres");
        }


        if (category.getCategoryType() == null || category.getCategoryType().toString().trim().isEmpty()) {
            throw new CategoryException("El tipo de la categoría es obligatorio y no puede estar vacío");
        }
        if (category.getCategoryType() == null){
            throw new CategoryException("El tipo de la categoría es inválido");
        }


        if (category.getDescription() != null && category.getDescription().length() > 255) {
            throw new CategoryException("La descripción de la categoría no puede exceder los 255 caracteres");
        }
    }

}

